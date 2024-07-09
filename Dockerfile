# Build Stage
FROM maven:3.9.8-eclipse-temurin-21 AS blog-api-builder

# Create app directory
RUN mkdir /app

# Copy necessary files for building the application
COPY pom.xml /app/pom.xml
COPY src /app/src
COPY .env /app/.env

# Set working directory
WORKDIR /app

# Build the application and skip tests
RUN mvn clean package -DskipTests=true

# Runtime Stage
FROM eclipse-temurin:21-jre

# Set working directory inside the container
WORKDIR /app

# Copy the packaged application from the build stage
COPY --from=blog-api-builder /app/target/blog-api-*.jar /app/blog-api.jar

# Copy the .env file to the runtime container
COPY .env /app/.env

# Copy the docker-compose.yml file to the runtime container
COPY docker-compose.yml /app/docker-compose.yml

# Copy the data folder to the runtime container
COPY data/keycloak /app/data/keycloak

# Specify the command to run the application
CMD ["java", "-jar", "/app/blog-api.jar", "--spring.profiles.active=dev"]

# Expose the application port
EXPOSE ${SERVER_PORT}