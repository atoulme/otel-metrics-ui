FROM openjdk:11.0.7-jre-slim-buster

COPY build/libs/metrics-ui* /opt/metrics.jar

ENTRYPOINT ["java", "-jar", "/opt/metrics.jar"]
