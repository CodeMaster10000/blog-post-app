package com.scalefocus.mk.blog.api.blog.integration;

import dasniko.testcontainers.keycloak.KeycloakContainer;
import io.github.cdimascio.dotenv.Dotenv;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@TestConfiguration
@Testcontainers
class TestContainersConfig {

    @Autowired
    private KeycloakConfig keycloakConfig;

    @Autowired
    private ConfigurableApplicationContext context;

    private static final Dotenv dotenv = Dotenv.load();

    private static final String MYSQL_IMAGE = dotenv.get("MYSQL_IMAGE");

    private static final String KEYCLOAK_IMAGE = dotenv.get("KEYCLOAK_IMAGE");

    static MySQLContainer<?> mySQLContainer = new MySQLContainer<>(MYSQL_IMAGE);

    static KeycloakContainer keycloakContainer =
            new KeycloakContainer(
                    KEYCLOAK_IMAGE)
                    .withRealmImportFile("keycloak/keycloak-blogapp-realm.json")
                    .withLogConsumer(new Slf4jLogConsumer(LoggerFactory.getLogger(KeycloakContainer.class)));

    @PostConstruct
    void startContainers() {
        CompletableFuture<Void> keycloakFuture = CompletableFuture.runAsync(() -> keycloakContainer.start());
        CompletableFuture<Void> mysqlFuture = CompletableFuture.runAsync(() -> mySQLContainer.start());
        CompletableFuture<Void> allFutures = CompletableFuture.allOf(keycloakFuture, mysqlFuture);
        allFutures.thenRun(this::configureContextForContainers).join();
    }

    @PreDestroy
    void stopContainers() {
        mySQLContainer.stop();
        keycloakContainer.stop();
    }

    private void configureContextForContainers() {
        configureContextForContainerizedKeycloak();
        configureLiquibaseContext();
        configureDatabaseContext();
    }

    private void configureContextForContainerizedKeycloak() {
        configureKeycloakContext();
        setKeycloakAuthUrl();
    }

    private void configureKeycloakContext() {
        String keycloakUrl = keycloakContainer.getAuthServerUrl();
        ConfigurableEnvironment environment = context.getEnvironment();
        Map<String, Object> map = new HashMap<>();
        map.put("spring.security.oauth2.client.provider.keycloak.issuer-uri", keycloakUrl + "realms/BlogApp");
        map.put("spring.security.oauth2.resourceserver.jwt.issuer-uri", keycloakUrl + "realms/BlogApp");
        map.put("keycloak.auth-server-url", keycloakUrl + "auth");
        environment.getPropertySources().addFirst(new MapPropertySource("dynamic", map));
    }

    private void configureLiquibaseContext() {
        ConfigurableEnvironment environment = context.getEnvironment();
        Map<String, Object> liquibaseProps = new HashMap<>();
        liquibaseProps.put("spring.liquibase.change-log", "classpath:/db/changelog/changelog-master.xml");
        liquibaseProps.put("spring.liquibase.url", mySQLContainer.getJdbcUrl());
        liquibaseProps.put("spring.liquibase.user", mySQLContainer.getUsername());
        liquibaseProps.put("spring.liquibase.password", mySQLContainer.getPassword());
        liquibaseProps.put("spring.liquibase.driver-class-name", "com.mysql.cj.jdbc.Driver");
        environment.getPropertySources().addFirst(new MapPropertySource("liquibaseProperties", liquibaseProps));
    }

    private void configureDatabaseContext() {
        ConfigurableEnvironment environment = context.getEnvironment();
        Map<String, Object> dataSourceProps = new HashMap<>();
        dataSourceProps.put("spring.datasource.url", mySQLContainer.getJdbcUrl());
        dataSourceProps.put("spring.datasource.username", mySQLContainer.getUsername());
        dataSourceProps.put("spring.datasource.password", mySQLContainer.getPassword());
        dataSourceProps.put("spring.datasource.driver-class-name", "com.mysql.cj.jdbc.Driver");
        environment.getPropertySources().addFirst(new MapPropertySource("datasourceProperties", dataSourceProps));
    }


    private void setKeycloakAuthUrl() {
        keycloakConfig.setKeycloakTokenUrl(keycloakContainer.getAuthServerUrl());
    }

}
