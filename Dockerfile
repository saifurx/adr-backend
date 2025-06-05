# Build stage
FROM maven:3.9.9-eclipse-temurin-22-alpine AS build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline

COPY src ./src
RUN mvn clean package -DskipTests

# Runtime stage
FROM amazoncorretto:22
WORKDIR /usr/local/lib

# Copy only the final JAR, avoiding unnecessary build artifacts
COPY --from=build /app/target/*.jar app.jar

# Run the application with optimized JVM settings
CMD ["java", "-XX:+UseContainerSupport", "-XX:MaxRAMPercentage=75", "-jar", "app.jar"]

# Expose application port
EXPOSE 7000