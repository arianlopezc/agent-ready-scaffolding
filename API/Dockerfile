# Build stage
FROM maven:3-eclipse-temurin-21 AS build
WORKDIR /build

# Copy POM files first for dependency caching
COPY pom.xml .
COPY Model/pom.xml Model/pom.xml
COPY SQLDatastore/pom.xml SQLDatastore/pom.xml
COPY Shared/pom.xml Shared/pom.xml
COPY API/pom.xml API/pom.xml

# Resolve dependencies (cached unless POMs change)
RUN mvn dependency:resolve -pl API -am -B 2>/dev/null || true

# Copy all source code
COPY Model/src Model/src
COPY SQLDatastore/src SQLDatastore/src
COPY Shared/src Shared/src
COPY API/src API/src

# Build the API module (skip tests for faster builds)
RUN mvn clean package -pl API -am -DskipTests -q

# Runtime stage
FROM eclipse-temurin:21-jre-alpine

# Create non-root user
RUN addgroup -S app && adduser -S app -G app

WORKDIR /app

# Copy fat jar from build stage
COPY --from=build /build/API/target/*.jar app.jar

# Set ownership
RUN chown -R app:app /app

USER app

# JVM flags for container environments
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0"

EXPOSE 8080

HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
    CMD wget -qO- http://localhost:8080/actuator/health || exit 1

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
