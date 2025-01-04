# Use an official OpenJDK image as a base
FROM openjdk:21-jdk-slim

# Set the working directory in the container
WORKDIR /app

# Install Docker CLI
RUN apt-get update && apt-get install -y \
    docker.io \
    && rm -rf /var/lib/apt/lists/*

# Copy the application JAR file
COPY build/libs/ExecutionService-0.0.1-SNAPSHOT.jar ExecutionService.jar

# Copy Docker files for different languages
COPY docker /app/ExecutionService/docker

# Expose the port the service runs on
EXPOSE 8084

# Set environment variables
ENV SPRING_PROFILES_ACTIVE=docker

# Command to run the application
ENTRYPOINT ["java", "-jar", "ExecutionService.jar"]