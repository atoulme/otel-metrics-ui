package io.opentelemetry.metricsui

import io.opentelemetry.proto.logs.v1.ResourceLogs
import io.opentelemetry.proto.metrics.v1.ResourceMetrics
import io.opentelemetry.proto.trace.v1.ResourceSpans
import org.springframework.stereotype.Service;
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.QueryParam

@Service
@Path("/rest")
class RestController(val logsSink : LogsSink, val metricsSink : MetricsSink, val spansSink : SpansSink) {

    @GET
    @Produces("application/json")
    @Path("/metrics")
    fun metrics(@QueryParam("start") start : Int = 0, @QueryParam("end") end : Int = 10): List<Metric> {
      return convertResourceMetrics(metricsSink.get(IntRange(start, end)))
    }

    @GET
    @Produces("application/json")
    @Path("/logs")
    fun logs(@QueryParam("start") start : Int = 0, @QueryParam("end") end : Int = 10): List<ResourceLogs> {
        return logsSink.get(IntRange(start, end))
    }

    @GET
    @Produces("application/json")
    @Path("/traces")
    fun traces(@QueryParam("start") start : Int = 0, @QueryParam("end") end : Int = 10): List<ResourceSpans> {
        return spansSink.get(IntRange(start, end))
    }

    private fun convertResourceMetrics(rls : List<ResourceMetrics>) : List<Metric> {
        val results = mutableListOf<Metric>()
        for (rl in rls) {
            for (ill in rl.instrumentationLibraryMetricsList) {
                for (m in ill.metricsList) {
                    val metric = Metric(m.name, metricValue(m))
                    results.add(metric)
                }
            }
        }
        return results
    }

    private fun metricValue(m: io.opentelemetry.proto.metrics.v1.Metric): String {
        if (m.hasGauge()) {
            return m.gauge.dataPointsList.map { it.asDouble }.joinToString(separator =  " ")
        }
        if (m.hasHistogram()) {
            return m.histogram.dataPointsList.map { "sum: ${it.sum}, count: ${it.count}, values: ${it.exemplarsList.map { it.asDouble }.joinToString(separator =  ",")}" }.joinToString(separator =  " ")
        }
        if (m.hasSum()) {
            return m.sum.dataPointsList.map { it.asDouble }.joinToString(separator =  " ")
        }

        if (m.hasSummary()) {
            return m.summary.dataPointsList.map { it.quantileValuesList }.joinToString(separator =  " ")
        }
        throw UnsupportedOperationException("Unsupported type for ${m.name}")

    }
}

data class Metric(val name : String, val value : String)
