# Use an available OpenJDK base image
FROM openjdk:21-slim

# Set the working directory in the container
WORKDIR /app

# Copy the project files to the working directory
COPY . .

# Package the application using Maven, skipping tests
RUN ./mvnw clean package -DskipTests

# Expose the port the application will run on
EXPOSE 8080

# Set the entry point to run the JAR file
ENTRYPOINT ["java", "-jar", "target/store-0.0.1-SNAPSHOT.jar"]