
# Blog API Project

This project is a Blog API built with Spring Boot. It leverages various technologies such as Keycloak for security, MySQL for database management, and Docker for containerization.

When you run the application, Docker runs the compose file which includes required services that the application depends on. Only after they have been fully initialized, up and running, does the application continue running.

## Prerequisites

Ensure you have the following installed:
- Docker
- Java 21
- Maven

## Setup Instructions

### Clone the repository

```bash
git clone https://github.com/CodeMaster10000/blog-post-app.git
```

Navigate into the project directory

```bash
cd blog-post-app/blog-api
```

### Maven

To build the project, run the following Maven command:

```sh
mvn clean install
```

### Environment Variables

You can modify the environment variables in the .env file located at the root of the project.

```env
# .env file

# Spring
SERVER_PORT=8081

# Database
MYSQL_IMAGE=mysql:8.0
MYSQL_PORT=4006
MYSQL_ROOT_PASSWORD=password
MYSQL_DATABASE=blogpost_db
MYSQL_HOST=localhost

# Keycloak
KEYCLOAK_IMAGE=quay.io/keycloak/keycloak:22.0.1
KEYCLOAK_PORT=8085
KEYCLOAK_ADMIN=admin
KEYCLOAK_ADMIN_PASSWORD=admin
KEYCLOAK_HOST=localhost
KEYCLOAK_CLIENT_ID=blog-api
KEYCLOAK_REALM=BlogApp
KEYCLOAK_USERNAME=beforecool
KEYCLOAK_PASSWORD=beforecool
KEYCLOAK_CLIENT_SECRET=4gJdZltVysucfICtMpr7dCktiQSUrOJ3
```

### Docker Compose

This is the Docker Compose configuration. You can modify it to configure infrastructure services.

```yaml
version: '3.8'

services:
  keycloak:
    image: ${KEYCLOAK_IMAGE}
    environment:
      KEYCLOAK_ADMIN: ${KEYCLOAK_ADMIN}
      KEYCLOAK_ADMIN_PASSWORD: ${KEYCLOAK_ADMIN_PASSWORD}
      KC_DB: mysql
      KC_DB_URL_DATABASE: ${MYSQL_DATABASE}
      KC_DB_URL_HOST: mysql  # This should refer to the MySQL service name
      KC_DB_URL_PORT: 3306
      KC_DB_USERNAME: root
      KC_DB_PASSWORD: ${MYSQL_ROOT_PASSWORD}
      KC_HTTP_PORT: ${KEYCLOAK_PORT}
    ports:
      - "${KEYCLOAK_PORT}:${KEYCLOAK_PORT}"
    volumes:
      - ./src/main/resources/keycloak/import-data.json:/opt/keycloak/data/import/keycloak-import-data.json
      - ./src/main/resources/keycloak/start-keycloak.sh:/opt/keycloak/bin/start-keycloak.sh
    entrypoint: ["/bin/bash", "/opt/keycloak/bin/start-keycloak.sh"]
    depends_on:
      - mysql

  mysql:
    image: ${MYSQL_IMAGE}
    environment:
      MYSQL_ROOT_PASSWORD: ${MYSQL_ROOT_PASSWORD}
      MYSQL_DATABASE: ${MYSQL_DATABASE}
    ports:
      - "${MYSQL_PORT}:3306"
    healthcheck:
      test: [ "CMD", "mysqladmin", "ping", "-h", "localhost" ]
      interval: 30s
      timeout: 10s
      retries: 5
    volumes:
      - mysql-data:/var/lib/mysql

volumes:
  mysql-data:

networks:
  default:
    driver: bridge
```

### How to Run

1. Start Docker and ensure it is running.
2. Build the project using Maven: `mvn clean install`.
3. Run the Spring Boot application from your IDE or using the command: `mvn spring-boot:run`.

### Important Notes

- All source and properties files read from the environment variables specified in the .env file.
- Ensure Docker, Java 21 and Maven are running before starting the application.
- The start-up might be slow because the application context waits for infrastructure provisioning.

### Running the Dockerized Image

You can download the Dockerized image from the following link:

[Download Dockerized Image](sandbox:/mnt/data/README.md)

After downloading, run the following command to start the container:

```sh
docker load -i blog-api-image.tar
docker run -d -p 8081:8081 blog-api-image
```