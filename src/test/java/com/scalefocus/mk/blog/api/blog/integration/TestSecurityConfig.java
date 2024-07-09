package com.scalefocus.mk.blog.api.blog.integration;

import com.scalefocus.mk.blog.api.core.security.keycloak.KeycloakLogoutHandler;
import com.scalefocus.mk.blog.api.core.security.keycloak.SecurityConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;

/**
 * Security configuration class for Keycloak integration used only in Testing phase / Test profile.
 * <p>
 * This class configures security settings for the application, including session management,
 * CORS settings, and authorization rules for different endpoints.
 * </p>
 */

@Configuration
@EnableWebSecurity
@Profile("test")
@DependsOn("dockerManager")
class TestSecurityConfig extends SecurityConfig {

    public TestSecurityConfig(OAuth2AuthorizedClientService authorizedClientService,
                              KeycloakLogoutHandler keycloakLogoutHandler) {
        super(authorizedClientService, keycloakLogoutHandler);
    }
}
