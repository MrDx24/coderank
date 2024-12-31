# Use an official OpenJDK image as a base
FROM openjdk:21-jdk-slim

# Set the working directory in the container
WORKDIR /app

# Copy the application JAR file into the container
COPY build/libs/ExecutionService-0.0.1-SNAPSHOT.jar ExecutionService.jar

# Expose the port the service runs on
EXPOSE 8084

# Set environment variables (optional, can be overridden during container run)
ENV SPRING_PROFILES_ACTIVE=docker

# Command to run the application
ENTRYPOINT ["java", "-jar", "ExecutionService.jar"]
