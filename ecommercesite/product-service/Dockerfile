# Multi-stage Dockerfile for Product Service
FROM eclipse-temurin:17-jdk-alpine AS build
WORKDIR /app
COPY pom.xml .
COPY common-lib/pom.xml ./common-lib/pom.xml
COPY product-service/pom.xml ./product-service/pom.xml
COPY common-lib/src ./common-lib/src
COPY product-service/src ./product-service/src
RUN apk add --no-cache maven && \
    mvn clean package -pl product-service -am -DskipTests -B

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
RUN addgroup -S appgroup && adduser -S appuser -G appgroup
COPY --from=build /app/product-service/target/*.jar app.jar
RUN chown appuser:appgroup app.jar
USER appuser
EXPOSE 8082
HEALTHCHECK --interval=30s --timeout=10s --retries=3 \
    CMD wget -qO- http://localhost:8082/actuator/health || exit 1
ENTRYPOINT ["java", "-jar", "-Djava.security.egd=file:/dev/./urandom", "app.jar"]
