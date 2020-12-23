package io.opentelemetry.metricsui

import io.grpc.Server
import io.grpc.netty.NettyServerBuilder
import io.grpc.stub.StreamObserver
import io.opentelemetry.proto.collector.logs.v1.ExportLogsServiceRequest
import io.opentelemetry.proto.collector.logs.v1.ExportLogsServiceResponse
import io.opentelemetry.proto.collector.logs.v1.LogsServiceGrpc
import io.opentelemetry.proto.collector.metrics.v1.ExportMetricsServiceRequest
import io.opentelemetry.proto.collector.metrics.v1.ExportMetricsServiceResponse
import io.opentelemetry.proto.collector.metrics.v1.MetricsServiceGrpc.MetricsServiceImplBase
import io.opentelemetry.proto.collector.trace.v1.ExportTraceServiceRequest
import io.opentelemetry.proto.collector.trace.v1.ExportTraceServiceResponse
import io.opentelemetry.proto.collector.trace.v1.TraceServiceGrpc.TraceServiceImplBase
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.net.InetSocketAddress
import javax.annotation.PostConstruct
import javax.annotation.PreDestroy

val logger = LoggerFactory.getLogger(MetricsServer::class.java)
@Component
class MetricsServer(@Value("\${grpc.port}")  val port : Int, @Value("\${grpc.host}")  val host : String, val spansCollector: SpansCollector, val metricsCollector: MetricsCollector, val logsCollector : LogsCollector) {


    private var server : Server? = null

    @PostConstruct
    fun start() {
        val builder = NettyServerBuilder.forAddress(InetSocketAddress(host, port))
        val newServer = builder.addService(spansCollector)
                .addService(metricsCollector)
                .addService(logsCollector)
                .build()
                .start()
        logger.info("Started grpc endpoint at ${host}:${port}")
        server = newServer
    }

    @PreDestroy
    fun close() {
        server?.shutdownNow()
    }

}

@Component
class SpansCollector(val sink : SpansSink) : TraceServiceImplBase() {
    override fun export(
            request: ExportTraceServiceRequest,
            responseObserver: StreamObserver<ExportTraceServiceResponse>) {
        sink.collect(request.resourceSpansList)
        responseObserver.onNext(ExportTraceServiceResponse.newBuilder().build())
        responseObserver.onCompleted()
    }
}

@Component
class MetricsCollector(val sink : MetricsSink) : MetricsServiceImplBase() {
    override fun export(
            request: ExportMetricsServiceRequest,
            responseObserver: StreamObserver<ExportMetricsServiceResponse>) {
        sink.collect(request.resourceMetricsList)
        responseObserver.onNext(ExportMetricsServiceResponse.newBuilder().build())
        responseObserver.onCompleted()
    }

}

@Component
class LogsCollector(val sink : LogsSink) : LogsServiceGrpc.LogsServiceImplBase() {
    override fun export(
            request: ExportLogsServiceRequest,
            responseObserver: StreamObserver<ExportLogsServiceResponse>) {
        sink.collect(request.resourceLogsList)
        responseObserver.onNext(ExportLogsServiceResponse.newBuilder().build())
        responseObserver.onCompleted()
    }

}
