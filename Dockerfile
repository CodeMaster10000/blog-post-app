# Use the official Maven image as the base image for the build stage
FROM maven:3.9.8-eclipse-temurin-21 AS blog-api-builder

RUN mkdir /app

# Copy the needed project files to the working directory (/app)
COPY pom.xml /app/pom.xml
COPY .env /app/.env
COPY docker-compose.yml /app/docker-compose.yml

# Copy the rest of the application source code to the working directory (/app)
COPY src app/src

WORKDIR /app

# Package the application, skipping tests
RUN mvn clean package -DskipTests

# Copy Liquibase configuration files to the appropriate location in the working directory (/app)
COPY src/main/resources/db/changelog /app/src/main/resources/db/changelog

# Use the official Eclipse Temurin JRE image as the base image for the runtime stage
FROM eclipse-temurin:21-jre

# Set the working directory inside the container
WORKDIR /app

# Copy the packaged application from the build stage
COPY --from=blog-api-builder /app/target/blog-api-*.jar ./blog-api.jar


# Specify the command to run the application
CMD ["java", "-jar", "blog-api.jar"]

# Expose the application port (optional, specify the port your application uses)
EXPOSE 8081
