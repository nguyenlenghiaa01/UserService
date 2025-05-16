# Use official OpenJDK runtime as a parent image
FROM openjdk:23-jdk-slim

# Set working directory in container
WORKDIR /app

# Copy jar file to container
COPY target/be.jar app.jar

# Expose port app chạy (8082 trong file config)
EXPOSE 8082

# Run the jar file
ENTRYPOINT ["java", "-jar", "app.jar"]
