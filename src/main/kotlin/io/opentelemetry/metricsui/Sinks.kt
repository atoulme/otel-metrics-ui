package io.opentelemetry.metricsui

import io.opentelemetry.proto.logs.v1.ResourceLogs
import io.opentelemetry.proto.metrics.v1.ResourceMetrics
import io.opentelemetry.proto.trace.v1.ResourceSpans

interface MetricsSink {

    fun get(range : IntRange) : List<ResourceMetrics>

    fun collect(metrics : List<ResourceMetrics>)

}

interface SpansSink {

    fun get(range : IntRange = IntRange(0,10)) : List<ResourceSpans>

    fun collect(traces : List<ResourceSpans>)

}

interface LogsSink {

    fun get(range : IntRange = IntRange(0,10)) : List<ResourceLogs>

    fun collect(logs : List<ResourceLogs>)

}
