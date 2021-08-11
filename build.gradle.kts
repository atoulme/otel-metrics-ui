import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	id("org.springframework.boot") version "2.4.1"
	id("io.spring.dependency-management") version "1.0.10.RELEASE"
	kotlin("jvm") version "1.4.21"
	kotlin("plugin.spring") version "1.4.21"
}

group = "io.opentelemetry"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_11

configurations {
	compileOnly {
		extendsFrom(configurations.annotationProcessor.get())
	}
}

repositories {
	mavenCentral()
}


val opentelemetryVersion = "1.4.1"
val grpcVersion = "1.34.1"


dependencies {
	implementation("org.springframework.boot:spring-boot-starter-jersey")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
	annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	implementation(platform("io.opentelemetry:opentelemetry-bom:${opentelemetryVersion}"))
	implementation(platform("io.grpc:grpc-bom:${grpcVersion}"))
	implementation("org.webjars:jquery:3.5.1")

	implementation("io.grpc:grpc-core")
	implementation("io.grpc:grpc-netty")
	implementation("io.grpc:grpc-stub")
	implementation("io.netty:netty-all")
	implementation("io.opentelemetry:opentelemetry-api")
	implementation("io.opentelemetry:opentelemetry-proto:${opentelemetryVersion}-alpha")
	implementation("io.opentelemetry:opentelemetry-sdk")
	implementation("io.opentelemetry:opentelemetry-sdk-trace")
	implementation("io.opentelemetry:opentelemetry-exporter-otlp-metrics:${opentelemetryVersion}-alpha")
	implementation("io.opentelemetry:opentelemetry-sdk-metrics:${opentelemetryVersion}-alpha")
}

tasks.withType<KotlinCompile> {
	kotlinOptions {
		freeCompilerArgs = listOf("-Xjsr305=strict")
		jvmTarget = "11"
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}
