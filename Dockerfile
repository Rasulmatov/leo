# Use an official Maven image to build the application
FROM maven:3.8.5-eclipse-temurin-17 AS build

# Set the working directory inside the container
WORKDIR /app

# Copy the Maven project files to the container
COPY pom.xml ./
COPY src ./src

# Build the Maven project
RUN mvn clean package -DskipTests

# Use an official Java runtime image to run the application
FROM eclipse-temurin:17-jdk

# Set the working directory inside the container
WORKDIR /app

# Copy the built JAR file from the build stage
COPY --from=build /app/target/*.jar app.jar

# Run the application
ENTRYPOINT ["java", "-jar","-Dspring.profiles.active=prod", "app.jar"]
