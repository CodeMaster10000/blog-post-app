package com.scalefocus.mk.blog.api.core.security.keycloak;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;

/**
 * Security configuration class for Keycloak integration used only OUTSIDE of Testing phase / Test profile.
 * <p>
 * This class configures security settings for the application, including session management,
 * CORS settings, and authorization rules for different endpoints.
 * </p>
 */
@Configuration
@EnableWebSecurity
@Profile("!test")
class ApplicationSecurityConfig extends SecurityConfig {

     ApplicationSecurityConfig(OAuth2AuthorizedClientService authorizedClientService, KeycloakLogoutHandler keycloakLogoutHandler) {
        super(authorizedClientService, keycloakLogoutHandler);
    }

}
