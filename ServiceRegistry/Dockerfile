# Use an official OpenJDK image as a base image
FROM openjdk:21-jdk-slim

# Set the working directory
WORKDIR /app

# Copy the build output to the container (replace with your actual JAR name)
COPY build/libs/ServiceRegistry-0.0.1-SNAPSHOT.jar service-registry.jar

# Expose the port that Eureka runs on
EXPOSE 8761

# Command to run the Eureka server application
ENTRYPOINT ["java", "-jar", "service-registry.jar"]
