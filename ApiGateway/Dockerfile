# Use a base image with Java installed
FROM openjdk:21-jdk-slim

# Set the working directory inside the container
WORKDIR /app

# Copy the built jar file to the container
COPY build/libs/ApiGateway-0.0.1-SNAPSHOT.jar api-gateway.jar

# Expose the port the service runs on
EXPOSE 8081

# Run the application
ENTRYPOINT ["java", "-jar", "api-gateway.jar"]
