# Stage 1: Build frontend
FROM node:20-alpine AS frontend-build

WORKDIR /app/frontend

# Copy frontend files
COPY frontend/package*.json ./
RUN npm ci

COPY frontend/ ./
RUN npm run build

# Stage 2: Build backend
FROM gradle:8.5-jdk17 AS build

WORKDIR /app

# Copy Gradle files
COPY build.gradle.kts settings.gradle.kts gradlew ./
COPY gradle/ gradle/

# Copy source code
COPY src/ src/

# Copy frontend build output to static resources
COPY --from=frontend-build /app/frontend/dist src/main/resources/static/

# Use template as application.yml if original doesn't exist
RUN if [ ! -f src/main/resources/application.yml ]; then \
      cp src/main/resources/application-template.yml src/main/resources/application.yml; \
    fi

# Build backend (skip tests for faster build)
RUN ./gradlew bootJar --no-daemon

# Stage 3: Runtime
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Create non-root user
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# Copy built jar from build stage
COPY --from=build /app/build/libs/*.jar app.jar

# Expose port
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
