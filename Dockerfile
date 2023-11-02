# Use a base image with Java
FROM openjdk:21

# Set the working directory inside the container
WORKDIR /app

# Copy the JAR file into the container
COPY target/kafka_irigation_system-1.0-SNAPSHOT.jar /app/app.jar

# Define the command to run your application
CMD ["java", "-jar", "app.jar"]