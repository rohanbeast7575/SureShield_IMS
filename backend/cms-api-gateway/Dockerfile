# Use a base image with JDK 17
FROM openjdk:17-jdk-alpine

# Set the working directory inside the container
WORKDIR /app

# Copy the Spring Boot JAR file into the container
COPY target/cms-api-gateway-0.0.1-SNAPSHOT.jar /app/cms-api-gateway-0.0.1-SNAPSHOT.jar

# Expose the port your service will run on (e.g., 8080)
EXPOSE 9000

# Run the Spring Boot application
ENTRYPOINT ["java", "-jar", "/app/cms-api-gateway-0.0.1-SNAPSHOT.jar"]
