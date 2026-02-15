# Multi-stage Dockerfile for Config Server
FROM eclipse-temurin:17-jdk-alpine AS build
WORKDIR /app
COPY pom.xml .
COPY common-lib/pom.xml ./common-lib/pom.xml
COPY config-server/pom.xml ./config-server/pom.xml
COPY common-lib/src ./common-lib/src
COPY config-server/src ./config-server/src
RUN apk add --no-cache maven && \
    mvn clean package -pl config-server -am -DskipTests -B

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
RUN addgroup -S appgroup && adduser -S appuser -G appgroup
COPY --from=build /app/config-server/target/*.jar app.jar
RUN chown appuser:appgroup app.jar
USER appuser
EXPOSE 8888
ENTRYPOINT ["java", "-jar", "app.jar"]
