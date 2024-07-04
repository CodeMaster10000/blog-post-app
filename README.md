
# Blog API

## Project Description

This is a Blog API application built with Spring Boot. The application allows users to create, update, delete, and view blog posts. Additionally, users can add tags to posts and retrieve posts by tags. The application uses Keycloak for security and authentication.

## Installation

### Prerequisites

- Java 21 or newer
- Maven
- Keycloak (for security)
- MySQL
- Vagrant (for Keycloak setup)
- VirtualBox

### Cloning the Repository

```sh
git clone https://github.com/CodeMaster10000/blog-post-app.git
cd blog-post-app
```

### Building the Project

```sh
cd blog-api
mvn clean install
```

### Configuration

#### Database setup

Make sure you have created the schema blogpost_db, so that the application can connect to it.
Open MySQL Workbench and create it.
You can also create it through MySQL Shell.

#### Keycloak Setup

1. Navigate to the keycloak-vagrant directory.
2. Start the Vagrant machine:

```sh
cd keycloak-vagrant
vagrant up
```

3. To suspend the Vagrant machine:

```sh
vagrant suspend
```

4. To resume the Vagrant machine:

```sh
vagrant resume
```

Ensure the Keycloak server is running and accessible at http://10.0.0.15:8081.

The default credentials are admin:admin
To override keycloak specific configuration, edit the environment.properties file inside the keycloak-vagrant directory.

#### Application Properties

Ensure you have the following configurations in your application.yml or application.properties:

```properties
spring.application.name=blog-api

# Swagger / OpenAPI
springdoc.api-docs.path=/api-docs
springdoc.swagger-ui.enabled=true
springdoc.swagger-ui.operationsSorter=method

# Database Connection Properties
spring.datasource.url=jdbc:mysql://localhost:3306/blogpost_db
spring.datasource.username=root
spring.datasource.password=password

# JDBC driver name and dialect
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# Hibernate settings
spring.jpa.database-platform=org.hibernate.dialect.MySQLDialect
spring.jpa.hibernate.ddl-auto=update

# Disable Open EntityManager in View
spring.jpa.open-in-view=false
management.endpoints.web.exposure.include=*

# Virtual Threads
spring.thread-executor=virtual

# Logging
logging.level.root=INFO
logging.level.org.springframework.boot=INFO
logging.level.org.springframework.security=DEBUG

# Security
spring.security.oauth2.client.registration.keycloak.client-id=blog-api
spring.security.oauth2.client.registration.keycloak.authorization-grant-type=authorization_code
spring.security.oauth2.client.registration.keycloak.scope=openid
spring.security.oauth2.client.provider.keycloak.issuer-uri=http://10.0.0.15:8081/realms/BlogApp
spring.security.oauth2.client.provider.keycloak.user-name-attribute=preferred_username
spring.security.oauth2.resourceserver.jwt.issuer-uri=http://10.0.0.15:8081/realms/BlogApp

# Keycloak client
keycloak.auth-server-url=http://10.0.0.15:8081/auth
keycloak.realm=BlogApp
admin-cli.client-id=blog-api
admin-cli.client-secret=codemaster
```


### Running the Application

#### Running Locally

```sh
cd blog-api
mvn spring-boot:run
```

## API Endpoints

To navigate to any of the endpoints you need to be authenticated via Keycloak,
the default admin user credentials for the blog api user are

 - username: blog-app-admin
 - password: codemaster

These can be modified within the Keycloak API or GUI.

### Blog Posts

- Create a Blog Post: `POST /api/v1/posts`
- Update a Blog Post: `PUT /api/v1/posts/{postId}`
- Get All Blog Posts: `GET /api/v1/posts`
- Get Blog Posts by Tag: `GET /api/v1/posts/tags/{tagName}`
- Add Tag to Blog Post: `POST /api/v1/posts/{postId}/tags/{tagName}`
- Remove Tag from Blog Post: `DELETE /api/v1/posts/{postId}/tags/{tagName}`