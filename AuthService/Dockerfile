# Use an official OpenJDK image as a base image
FROM openjdk:21-jdk-slim

# Set the working directory
WORKDIR /app

# Copy the build output to the container (replace with your actual JAR name)
COPY build/libs/AuthService-0.0.1-SNAPSHOT.jar auth-service.jar

# Expose the port that the AuthService runs on
EXPOSE 8082

# Command to run the AuthService application
ENTRYPOINT ["java", "-jar", "auth-service.jar"]
