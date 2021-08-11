package io.opentelemetry.metricsui

import io.opentelemetry.proto.metrics.v1.Metric
import org.springframework.stereotype.Component

@Component
class MetricsListView(val metricsSink: MetricsSink) {

    fun getMetrics() : List<String> {
        val results = mutableListOf<String>()
        val rls = metricsSink.get(IntRange(0,10))
        for (rl in rls) {
            for (ill in rl.instrumentationLibraryMetricsList) {
                for (m in ill.metricsList) {
                    val metricString = "${m.name} ${metricValue(m)}"
                    results.add(metricString)
                }
            }
        }
        return results
    }

    private fun metricValue(m: Metric): String {
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
