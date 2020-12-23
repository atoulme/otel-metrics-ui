package io.opentelemetry.metricsui

import org.glassfish.jersey.server.ResourceConfig
import org.glassfish.jersey.servlet.ServletProperties
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Configuration


@SpringBootApplication
class MetricsUiApplication

fun main(args: Array<String>) {
	runApplication<MetricsUiApplication>(*args)
}

@Configuration
class JerseyConfig : ResourceConfig() {
	init {
		register(RestController::class.java)
		property(ServletProperties.FILTER_FORWARD_ON_404, true);
	}

}
