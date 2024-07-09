package com.scalefocus.mk.blog.api.core.security.keycloak;

import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for setting up the Keycloak client.
 * <p>
 * This class configures the Keycloak client using properties defined in the application's configuration
 * and provides a {@link Keycloak} bean for interacting with the Keycloak server.
 * </p>
 */
@Configuration
class KeycloakClientConfig {

    private final String serverUrl;
    private final String realm;
    private final String clientId;
    private final String clientSecret;

    KeycloakClientConfig(
            @Value("${keycloak.auth-server-url}") String serverUrl,
            @Value("${keycloak.realm}") String realm,
            @Value("${admin-cli.client-id}") String clientId,
            @Value("${admin-cli.client-secret}") String clientSecret) {
        this.serverUrl = serverUrl;
        this.realm = realm;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
    }

    /**
     * Creates and configures a {@link Keycloak} client.
     * <p>
     * This method builds a Keycloak client using the provided server URL, realm, client ID, and client secret.
     * It uses the client credentials grant type to authenticate with the Keycloak server.
     * </p>
     *
     * @return a configured {@link Keycloak} client
     */
    @Bean
    public Keycloak keycloak() {
        return KeycloakBuilder.builder()
                .serverUrl(serverUrl)
                .realm(realm)
                .clientId(clientId)
                .clientSecret(clientSecret)
                .grantType(OAuth2Constants.CLIENT_CREDENTIALS)
                .build();
    }
}
