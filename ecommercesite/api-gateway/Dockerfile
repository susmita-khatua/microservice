# Multi-stage Dockerfile for API Gateway
FROM eclipse-temurin:17-jdk-alpine AS build
WORKDIR /app
COPY pom.xml .
COPY common-lib/pom.xml ./common-lib/pom.xml
COPY api-gateway/pom.xml ./api-gateway/pom.xml
COPY common-lib/src ./common-lib/src
COPY api-gateway/src ./api-gateway/src
RUN apk add --no-cache maven && \
    mvn clean package -pl api-gateway -am -DskipTests -B

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
RUN addgroup -S appgroup && adduser -S appuser -G appgroup
COPY --from=build /app/api-gateway/target/*.jar app.jar
RUN chown appuser:appgroup app.jar
USER appuser
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "-Djava.security.egd=file:/dev/./urandom", "app.jar"]
