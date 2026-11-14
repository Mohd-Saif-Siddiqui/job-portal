# One image that contains both the React frontend and the Spring Boot backend.

# 1) Build the React app
FROM node:20-alpine AS web
WORKDIR /web
COPY frontend/package*.json ./
RUN npm ci
COPY frontend/ ./
RUN npm run build

# 2) Build the Spring Boot jar, with the React build bundled as static files
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY backend/pom.xml ./
RUN mvn -q -B dependency:go-offline
COPY backend/src ./src
COPY --from=web /web/dist ./src/main/resources/static
RUN mvn -q -B -DskipTests package

# 3) Small runtime image
FROM eclipse-temurin:17-jre
WORKDIR /app
RUN useradd --system --create-home appuser && mkdir -p /app/uploads && chown -R appuser /app
COPY --from=build /app/target/job-portal-backend-*.jar app.jar
USER appuser
# Keep memory low so it fits free tiers (512 MB).
ENV JAVA_TOOL_OPTIONS="-XX:MaxRAMPercentage=65 -XX:+UseSerialGC -Xss512k"
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
