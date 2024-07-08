package com.scalefocus.mk.blog.api.core.config;

import lombok.Getter;
import org.springframework.stereotype.Component;

import org.springframework.beans.factory.annotation.Value;

@Component
@Getter
final class DockerDataConfig {

    private final String keycloakUrl;
    private final String mySqlUrl;
    private final String mySqlUser;
    private final String mySqlPassword;

    public DockerDataConfig(
            @Value("${spring.security.oauth2.client.provider.keycloak.issuer-uri}") String keycloakUrl,
            @Value("${spring.datasource.url}") String mySqlUrl,
            @Value("${spring.datasource.username}") String mySqlUser,
            @Value("${spring.datasource.password}")String mySqlPassword) {
        this.keycloakUrl = keycloakUrl;
        this.mySqlUrl = mySqlUrl;
        this.mySqlUser = mySqlUser;
        this.mySqlPassword = mySqlPassword;
    }

}
