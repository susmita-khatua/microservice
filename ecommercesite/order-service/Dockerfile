# Multi-stage Dockerfile for Order Service
FROM eclipse-temurin:17-jdk-alpine AS build
WORKDIR /app
COPY pom.xml .
COPY common-lib/pom.xml ./common-lib/pom.xml
COPY order-service/pom.xml ./order-service/pom.xml
COPY common-lib/src ./common-lib/src
COPY order-service/src ./order-service/src
RUN apk add --no-cache maven && \
    mvn clean package -pl order-service -am -DskipTests -B

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
RUN addgroup -S appgroup && adduser -S appuser -G appgroup
COPY --from=build /app/order-service/target/*.jar app.jar
RUN chown appuser:appgroup app.jar
USER appuser
EXPOSE 8084
HEALTHCHECK --interval=30s --timeout=10s --retries=3 \
    CMD wget -qO- http://localhost:8084/actuator/health || exit 1
ENTRYPOINT ["java", "-jar", "-Djava.security.egd=file:/dev/./urandom", "app.jar"]
