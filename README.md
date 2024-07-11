
# Blog API Project

This project is a Blog API built with Spring Boot.
It leverages various technologies such as Keycloak for security, 
MySQL for database management, RabbitMQ, ElasticSearch and Docker for containerization.

## Prerequisites

Ensure you have the following installed:
- Docker (running)
- Java 21
- Maven

## Setup Instructions

### Clone the repository

```bash
git clone https://github.com/CodeMaster10000/blog-post-app.git
```

Navigate into the project directory

```bash
cd blog-post-app
```

### Maven

To build the project, run the following Maven command:

```sh
mvn clean package
```

### Environment Variables

The entire project uses a centralized environment file.
To change any property, navigate to the .env file at the root of the project.

```env
# General
SERVER_PORT=8081

# Database
MYSQL_IMAGE=mysql:8.0
MYSQL_PORT=4006
MYSQL_INTERNAL_PORT=3306
MYSQL_ROOT_PASSWORD=password
MYSQL_DATABASE=blogpost_db
MYSQL_HOST=mysql

# Keycloak
KEYCLOAK_IMAGE=quay.io/keycloak/keycloak:22.0.1
KEYCLOAK_PORT=8085
KEYCLOAK_ADMIN=admin
KEYCLOAK_ADMIN_PASSWORD=admin
KEYCLOAK_HOST=keycloak
KEYCLOAK_CLIENT_ID=blog-api
KEYCLOAK_REALM=BlogApp
KEYCLOAK_USERNAME=beforecool
KEYCLOAK_PASSWORD=beforecool
KEYCLOAK_CLIENT_SECRET=4gJdZltVysucfICtMpr7dCktiQSUrOJ3
KEYCLOAK_DATABASE=keycloak_db

# Elastic Search
ELASTICSEARCH_IMAGE=elasticsearch:8.8.0
ELASTICSEARCH_API_PORT=8095
ELASTICSEARCH_INTERNAL_API_PORT=9200
ELASTICSEARCH_CLUSTER_PORT=9300

# RabbitMQ
RABBITMQ_DEFAULT_USER=user
RABBITMQ_DEFAULT_PASS=user
RABBITMQ_PORT=5672
```

### Docker Compose

The application depends on the following infrastructure, described in the container/docker-compose.yml file.
This is the Docker Compose configuration. It contains five services.

- MySQL
- Keycloak
- RabbitMQ
- ElasticSearch
- Blog-API

You can modify it to configure services.

```yaml
version: '3.8'

services:
  keycloak:
    image: ${KEYCLOAK_IMAGE}
    container_name: keycloak
    environment:
      KEYCLOAK_ADMIN: ${KEYCLOAK_ADMIN}
      KEYCLOAK_ADMIN_PASSWORD: ${KEYCLOAK_ADMIN_PASSWORD}
      KC_DB: mysql
      KC_DB_URL_DATABASE: ${KEYCLOAK_DATABASE}
      KC_DB_URL_HOST: mysql
      KC_DB_URL_PORT: ${MYSQL_INTERNAL_PORT}
      KC_DB_USERNAME: root
      KC_DB_PASSWORD: ${MYSQL_ROOT_PASSWORD}
      KC_HTTP_PORT: ${KEYCLOAK_PORT}
    ports:
      - "${KEYCLOAK_PORT}:${KEYCLOAK_PORT}"
    volumes:
      - ./data/config/keycloak:/opt/keycloak/data
    entrypoint: ["/bin/bash", "/opt/keycloak/data/start-keycloak.sh"]
    depends_on:
      - mysql

  mysql:
    image: ${MYSQL_IMAGE}
    container_name: mysql
    environment:
      KEYCLOAK_DATABASE: ${KEYCLOAK_DATABASE}
      MYSQL_ROOT_PASSWORD: ${MYSQL_ROOT_PASSWORD}
      MYSQL_DATABASE: ${MYSQL_DATABASE}
    ports:
      - "${MYSQL_PORT}:${MYSQL_INTERNAL_PORT}"
    healthcheck:
      test: ["CMD", "mysqladmin", "ping", "-h", "localhost"]
      interval: 30s
      timeout: 10s
      retries: 5
    volumes:
      - mysql_data:/var/lib/mysql
      - ./data/config/mysql/mysql_init.sh:/docker-entrypoint-initdb.d/mysql_init.sh

  elasticsearch:
    image: ${ELASTICSEARCH_IMAGE}
    container_name: elasticsearch
    environment:
      - discovery.type=single-node
    ports:
      - ${ELASTICSEARCH_API_PORT}:${ELASTICSEARCH_INTERNAL_API_PORT}
      - ${ELASTICSEARCH_CLUSTER_PORT}:${ELASTICSEARCH_CLUSTER_PORT}
    volumes:
      - elastic_data:/usr/share/elasticsearch/data

  rabbitmq:
    image: rabbitmq:3-management
    container_name: rabbitmq
    ports:
      - "${RABBITMQ_PORT}:${RABBITMQ_PORT}"
      - "15672:15672"
    environment:
      RABBITMQ_DEFAULT_USER: ${RABBITMQ_DEFAULT_USER}
      RABBITMQ_DEFAULT_PASS: ${RABBITMQ_DEFAULT_PASS}
    volumes:
      - rabbitmq_data:/var/lib/rabbitmq

  blog-api:
    image: blog-api:0.2.7
    container_name: blog-api
    environment:
      SERVER_PORT: ${SERVER_PORT}
      MYSQL_HOST: mysql
      MYSQL_PORT: ${MYSQL_INTERNAL_PORT}
      MYSQL_DATABASE: ${MYSQL_DATABASE}
      MYSQL_USERNAME: root
      MYSQL_PASSWORD: ${MYSQL_ROOT_PASSWORD}
      KEYCLOAK_HOST: keycloak
      KEYCLOAK_PORT: ${KEYCLOAK_PORT}
      ELASTICSEARCH_HOST: elasticsearch
      ELASTICSEARCH_API_PORT: ${ELASTICSEARCH_INTERNAL_API_PORT}
      RABBITMQ_HOST: rabbitmq
      RABBITMQ_PORT: ${RABBITMQ_PORT}
    ports:
      - "${SERVER_PORT}:${SERVER_PORT}"
    depends_on:
      - keycloak
      - mysql
      - elasticsearch
      - rabbitmq
    volumes:
      - ./data/config/keycloak:/app/data/keycloak
    entrypoint: ["/bin/bash", "/app/data/keycloak/wait-for-keycloak.sh", "java", "-jar", "blog-api.jar", "--spring.profiles.active=dev"]

volumes:
  mysql_data:
  elastic_data:
  rabbitmq_data:
```

## How to Run

### Notice!

There are TWO .env files.

- In the `container` directory 
- In the root directory.

The one in the `container` directory applies to the `Fully Containerized` setup.

The one in the root directory applies to `Containerized Infrastructure` setup

### Fully Containerized

To run the service within a docker container:

1. Start Docker and ensure it is running.
2. Navigate to the root directory
3. Create a Docker image from the Dockerfile

    ```bash
      docker build -t blog-api:0.2.7
    ```

4. go to `container` package and run `docker-compose up` in the container package
5. Ensure that the containers and services within are up and running
6. Login on Keycloak http://localhost:8085/auth (admin:admin)
7. Use the application

### Containerized Infrastructure

To run the service outside a container:

1. Start Docker and ensure it is running.
2. go to `container` package and run `docker-compose up --scale blog-api=0` in the container package
3. Ensure that the containers and services within are up and running
4. Login on Keycloak http://localhost:8085/auth (admin:admin)
5. Make sure to change the spring profile to dev in application.properties -> `spring.profiles.active=dev`
6. Run the application from an IDE or java -jar /target/blog-api-0.2.7-SNAPSHOT.jar
7. Use the application

### Using the application

Go to any of these end-points,
Keycloak should prompt you to login.
If you do not have an account, click on 'register' to create a new account.
You are good to go!

- Create a Blog Post: `POST /api/v1/posts`
- Update a Blog Post: `PUT /api/v1/posts/{postId}`
- Get All Blog Posts: `GET /api/v1/posts`
- Get Blog Posts by Tag: `GET /api/v1/posts/tags/{tagName}`
- Add Tag to Blog Post: `POST /api/v1/posts/{postId}/tags/{tagName}`
- Remove Tag from Blog Post: `DELETE /api/v1/posts/{postId}/tags/{tagName}`


### Important Notes

- All source and properties files read from the environment variables specified in the `.env` or `container/.env` file.
- Ensure Docker, Java 21 and Maven are running before starting the application.
- Before starting the application, make sure the containers for the infrastructure are healthy.

### Clear state

After shutting down the application, if you'd like to remove the infrastructure,

go to the `container` directory and run

```bash
   docker-compose down -v`
```

Although, if you only want to put them to sleep,

```bash
   docker-compose stop`
```