package io.opentelemetry.metricsui

import io.opentelemetry.proto.logs.v1.ResourceLogs
import io.opentelemetry.proto.metrics.v1.ResourceMetrics
import io.opentelemetry.proto.trace.v1.ResourceSpans
import org.springframework.stereotype.Component

internal class LRUCache<K, V>(private val cacheSize : Int) : LinkedHashMap<K, V>(16, 0.75F, true) {

    override fun removeEldestEntry(eldest : Map.Entry<K, V>): Boolean {
        return size >= cacheSize;
    }
}

@Component
class InMemoryLogsSink : LogsSink {

    internal val logCache = LRUCache<Int, ResourceLogs>(10000)
    override fun get(range: IntRange): List<ResourceLogs> {
        val result = mutableListOf<ResourceLogs>()
        val values = logCache.values
        for (index in range) {
            if (index >= 0 && index < values.size) {
                result.add(values.elementAt(index))
            }
        }
        return result
    }

    override fun collect(logs: List<ResourceLogs>) {
        for (rl in logs) {
            logCache.put(rl.hashCode(), rl)
        }
    }
}

@Component
class InMemoryMetricsSink : MetricsSink {

    internal val metricsCache = LRUCache<Int, ResourceMetrics>(10000)
    override fun get(range: IntRange): List<ResourceMetrics> {
        val result = mutableListOf<ResourceMetrics>()
        val values = metricsCache.values
        for (index in range) {
            if (index >= 0 && index < values.size) {
                result.add(values.elementAt(index))
            }
        }
        return result
    }

    override fun collect(metrics: List<ResourceMetrics>) {
        for (metric in metrics) {
            metricsCache.put(metric.hashCode(), metric)
        }
    }
}

@Component
class InMemorySpansSink : SpansSink {

    internal val spansCache = LRUCache<Int, ResourceSpans>(10000)

    override fun get(range: IntRange): List<ResourceSpans> {
        val result = mutableListOf<ResourceSpans>()
        val values = spansCache.values
        for (index in range) {
            if (index >= 0 && index < values.size) {
                result.add(values.elementAt(index))
            }
        }
        return result
    }


    override fun collect(traces: List<ResourceSpans>) {
        for (trace in traces) {
            spansCache.put(trace.hashCode(), trace)
        }
    }
}
