package io.opentelemetry.metricsui

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class MetricsUiApplication

fun main(args: Array<String>) {
	runApplication<MetricsUiApplication>(*args)
}
