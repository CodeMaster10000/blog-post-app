package com.scalefocus.mk.blog.api.blog.integration;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Configuration class for Keycloak settings.
 */
@Getter
@Component
final class KeycloakConfig {

    private final String keycloakRealm;
    private final String keycloakUsername;
    private final String keycloakPassword;
    private final String clientId;
    private final String keycloakClientSecret;
    private String keycloakTokenUrl;

    KeycloakConfig(
            @Value("${keycloak.realm}") String keycloakRealm,
            @Value("${keycloak-username}") String keycloakUsername,
            @Value("${admin-cli.client-secret}") String keycloakClientSecret,
            @Value("${keycloak-password}") String keycloakPassword,
            @Value("${admin-cli.client-id}") String clientId) {
        this.keycloakRealm = keycloakRealm;
        this.keycloakClientSecret = keycloakClientSecret;
        this.keycloakUsername = keycloakUsername;
        this.keycloakPassword = keycloakPassword;
        this.clientId = clientId;
    }

    void setKeycloakTokenUrl(String authServerUrl) {
        this.keycloakTokenUrl = String.format("%srealms/%s/protocol/openid-connect/token", authServerUrl, this.keycloakRealm);
    }
}
