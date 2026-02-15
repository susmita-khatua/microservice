# Multi-stage Dockerfile for Aggregation Service
FROM eclipse-temurin:17-jdk-alpine AS build
WORKDIR /app
COPY pom.xml .
COPY common-lib/pom.xml ./common-lib/pom.xml
COPY aggregation-service/pom.xml ./aggregation-service/pom.xml
COPY common-lib/src ./common-lib/src
COPY aggregation-service/src ./aggregation-service/src
RUN apk add --no-cache maven && \
    mvn clean package -pl aggregation-service -am -DskipTests -B

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
RUN addgroup -S appgroup && adduser -S appuser -G appgroup
COPY --from=build /app/aggregation-service/target/*.jar app.jar
RUN chown appuser:appgroup app.jar
USER appuser
EXPOSE 8087
ENTRYPOINT ["java", "-jar", "-Djava.security.egd=file:/dev/./urandom", "app.jar"]
