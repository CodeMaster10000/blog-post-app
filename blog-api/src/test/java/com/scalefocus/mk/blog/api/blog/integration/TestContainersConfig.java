package com.scalefocus.mk.blog.api.blog.integration;

import dasniko.testcontainers.keycloak.KeycloakContainer;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.HashMap;
import java.util.Map;

@TestConfiguration
@Testcontainers
class TestContainersConfig {

    @Autowired
    private KeycloakConfig keycloakConfig;

    @Autowired
    private ConfigurableApplicationContext context;

    @ServiceConnection
    static MySQLContainer<?> mySQLContainer = new MySQLContainer<>("mysql:latest");

    static KeycloakContainer keycloakContainer =
            new KeycloakContainer(
                    "keycloak/keycloak:22.0.1")
                    .withRealmImportFile("keycloak/keycloak-blogapp-realm.json")
                    .withLogConsumer(new Slf4jLogConsumer(LoggerFactory.getLogger(KeycloakContainer.class)));

    @PostConstruct
    void startContainers() {
        keycloakContainer.start();
        mySQLContainer.start();
        configureContextForContainerizedKeycloak();
    }

    private void configureContextForContainerizedKeycloak() {
        configureKeycloakContext();
        setKeycloakAuthUrl();
    }

    @PreDestroy
    void stopContainers() {
        mySQLContainer.stop();
        keycloakContainer.stop();
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

    private void setKeycloakAuthUrl() {
        keycloakConfig.setKeycloakTokenUrl(keycloakContainer.getAuthServerUrl());
    }

}
