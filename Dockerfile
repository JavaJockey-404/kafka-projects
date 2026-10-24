# Use official OpenJDK 21 image
FROM eclipse-temurin:21-jdk

# Set working directory inside container
WORKDIR /appAbhishek

# Copy the built JAR file into the container
COPY target/spring-boot-2-hello-world-1.0.2-SNAPSHOT.jar appAbhishek.jar

# Expose the port your Spring Boot app runs on
EXPOSE 9729

# Run the JAR
ENTRYPOINT ["java", "-jar", "appAbhishek.jar"]
