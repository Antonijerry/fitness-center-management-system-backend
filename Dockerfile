# =========================
# Build stage
# =========================
FROM maven:3.9.11-eclipse-temurin-21 AS build

WORKDIR /app

# Copy Maven configuration first for better Docker layer caching
COPY pom.xml .

# Download dependencies
RUN mvn dependency:go-offline -B

# Copy source code
COPY src ./src

# Build the Spring Boot application
RUN mvn clean package -DskipTests


# =========================
# Runtime stage
# =========================
FROM eclipse-temurin:21-jre AS runtime

WORKDIR /app

# Create a non-root user
RUN useradd --system --create-home --shell /bin/false spring

# Copy the generated JAR from the build stage
COPY --from=build /app/target/*.jar app.jar

# Give the application user ownership
RUN chown spring:spring app.jar

# Run as non-root user
USER spring

# Spring Boot port
EXPOSE 8080

# Start the application
ENTRYPOINT ["java", "-jar", "app.jar"]