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
        if (m.hasDoubleGauge()) {
            return m.doubleGauge.dataPointsList.map { it.value }.joinToString(separator =  " ")
        }
        if (m.hasIntGauge()) {
            return m.intGauge.dataPointsList.map { it.value }.joinToString(separator =  " ")
        }
        if (m.hasDoubleHistogram()) {
            return m.doubleHistogram.dataPointsList.map { "sum: ${it.sum}, count: ${it.count}, values: ${it.exemplarsList.map { it.value }.joinToString(separator =  ",")}" }.joinToString(separator =  " ")
        }
        if (m.hasIntHistogram()) {
            return m.intHistogram.dataPointsList.map { "sum: ${it.sum}, count: ${it.count}, values: ${it.exemplarsList.map { it.value }.joinToString(separator =  ",")}" }.joinToString(separator =  " ")
        }
        if (m.hasDoubleSum()) {
            return m.doubleSum.dataPointsList.map { it.value }.joinToString(separator =  " ")
        }
        if (m.hasIntSum()) {
            return m.intSum.dataPointsList.map { it.value }.joinToString(separator =  " ")
        }
        throw UnsupportedOperationException("Unsupported type for ${m.name}")

    }
}
