# Use an official OpenJDK image as a base
FROM openjdk:21-jdk-slim

# Set the working directory in the container
WORKDIR /app

# Copy the application JAR file into the container
COPY build/libs/TaskQueueService-0.0.1-SNAPSHOT.jar TaskQueueService.jar

# Expose the port the service runs on
EXPOSE 8083

# Set environment variables (optional, can be overridden during container run)
ENV SPRING_PROFILES_ACTIVE=docker

# Command to run the application
ENTRYPOINT ["java", "-jar", "TaskQueueService.jar"]
