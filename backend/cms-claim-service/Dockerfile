# Use a base image with JDK 17
FROM openjdk:17-jdk-alpine

# Set the working directory inside the container
WORKDIR /app

# Copy the Spring Boot JAR file into the container
COPY target/cms-claim-service-0.0.1-SNAPSHOT.jar /app/cms-claim-service-0.0.1-SNAPSHOT.jar

# Expose the port your service will run on (e.g., 8080)
EXPOSE 8083

# Run the Spring Boot application
ENTRYPOINT ["java", "-jar", "/app/cms-claim-service-0.0.1-SNAPSHOT.jar"]
