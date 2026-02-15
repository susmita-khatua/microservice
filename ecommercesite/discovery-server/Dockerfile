# Multi-stage Dockerfile for Discovery Server
FROM eclipse-temurin:17-jdk-alpine AS build
WORKDIR /app
COPY pom.xml .
COPY common-lib/pom.xml ./common-lib/pom.xml
COPY discovery-server/pom.xml ./discovery-server/pom.xml
COPY common-lib/src ./common-lib/src
COPY discovery-server/src ./discovery-server/src
RUN apk add --no-cache maven && \
    mvn clean package -pl discovery-server -am -DskipTests -B

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
RUN addgroup -S appgroup && adduser -S appuser -G appgroup
COPY --from=build /app/discovery-server/target/*.jar app.jar
RUN chown appuser:appgroup app.jar
USER appuser
EXPOSE 8761
ENTRYPOINT ["java", "-jar", "app.jar"]
