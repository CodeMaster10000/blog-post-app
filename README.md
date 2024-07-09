
# Blog API Project

This project is a Blog API built with Spring Boot.
It leverages various technologies such as Keycloak for security, 
MySQL for database management, RabbitMQ, ElasticSearch and Docker for containerization.

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
cd blog-post-app
```

### Maven

To build the project, without running the tests, run the following Maven command:

```sh
mvn clean package
```

To build the project while running all the tests:

```sh

mvn clean package -Ptest

```

### Environment Variables

The entire project uses a centralized environment file.
To change any property, navigate to the .env file at the root of the project.

```env
# .env file

# General
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

# Elastic Search
ELASTICSEARCH_IMAGE=elasticsearch:8.8.0
ELASTICSEARCH_API_PORT=8095
ELASTICSEARCH_CLUSTER_PORT=9300

# RabitMQ
RABBITMQ_DEFAULT_USER=user
RABBITMQ_DEFAULT_PASS=user
```

### Docker Compose

The application depends on the following infrastructure, described in the docker-compose.yml file.
This is the Docker Compose configuration. It contains two services, mysql and keycloak.
You can modify it to configure infrastructure services.

```yaml
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
      - ./data/keycloak/import-data.json:/opt/keycloak/data/import/keycloak-import-data.json
      - ./data/keycloak/start-keycloak.sh:/opt/keycloak/bin/start-keycloak.sh
    entrypoint: ["/bin/bash", "/opt/keycloak/bin/start-keycloak.sh"]

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
      - ./data/mysql:/var/lib/mysql

  elasticsearch:
    image: ${ELASTICSEARCH_IMAGE}
    container_name: elasticsearch
    environment:
      - discovery.type=single-node
    ports:
      - ${ELASTICSEARCH_API_PORT}:9200
      - ${ELASTICSEARCH_CLUSTER_PORT}:9300
    volumes:
      - ./data/elastic:/usr/share/elasticsearch/data

  rabbitmq:
    image: rabbitmq:3-management
    container_name: rabbitmq
    ports:
      - "5672:5672"
      - "15672:15672"
    environment:
      RABBITMQ_DEFAULT_USER: ${RABBITMQ_DEFAULT_USER}
      RABBITMQ_DEFAULT_PASS: ${RABBITMQ_DEFAULT_PASS}
    volumes:
      - ./data/rabbitmq:/var/lib/rabbitmq

networks:
  default:
    driver: bridge
```

### How to Run

1. Start Docker and ensure it is running.
2. Run `docker-compose up` in the root directory of the project
3. Ensure that the containers and services within are up and running
4. Login on Keycloak http://localhost:8085/auth (admin:admin)
5. Run the Spring Boot application from your IDE or using the command: `mvn spring-boot:run`.

### Important Notes

- All source and properties files read from the environment variables specified in the .env file.
- Ensure Docker, Java 21 and Maven are running before starting the application.
- Before starting the application, make sure the containers for the infrastructure are healthy.

### Clear state

After shutting down the application, if you'd like to remove the infrastructure,

run `docker-compose down -v`

Although, if you only want to put them to sleep,

run `docker-compose stop`

### Using the application

Go to any of these end-points,
Keycloak should prompt you to login.
If you do not have an account, click on 'register' to create a new account.

- Create a Blog Post: `POST /api/v1/posts`
- Update a Blog Post: `PUT /api/v1/posts/{postId}`
- Get All Blog Posts: `GET /api/v1/posts`
- Get Blog Posts by Tag: `GET /api/v1/posts/tags/{tagName}`
- Add Tag to Blog Post: `POST /api/v1/posts/{postId}/tags/{tagName}`
- Remove Tag from Blog Post: `DELETE /api/v1/posts/{postId}/tags/{tagName}`