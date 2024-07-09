package com.scalefocus.mk.blog.api.blog.integration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Date;

/**
 * Client class for handling JWT tokens with Keycloak.
 * <p>
 * This class provides methods to obtain and validate JWT tokens from Keycloak.
 * </p>
 */
@Component
final class KeycloakClient {

    private final KeycloakConfig keycloakConfig;

    private String jwt;

    private static final Logger logger = LoggerFactory.getLogger(KeycloakClient.class);

    KeycloakClient(KeycloakConfig keycloakConfig) {
        this.keycloakConfig = keycloakConfig;
    }

    /**
     * Gets the JWT token.
     * <p>
     * This method returns the existing JWT token if it is valid, otherwise it obtains a new token from Keycloak.
     * </p>
     *
     * @return the JWT token
     */
    String getJwtToken() {
        if (jwt == null || !isTokenValid()) {
            try {
                jwt = getNewTokenFromSecurityProvider();
            } catch (JsonProcessingException e) {
                logger.error(e.getMessage());
            }
        }
        return jwt;
    }

    String getKeycloakUsername() {
        return this.keycloakConfig.getKeycloakUsername();
    }

    private boolean isTokenValid() {
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(keycloakConfig.getKeycloakClientSecret())
                    .parseClaimsJws(jwt)
                    .getBody();
            Date expiration = claims.getExpiration();
            return expiration.after(new Date());
        } catch (SignatureException | IllegalArgumentException e) {
            logger.error("Token validation error: {}", e.getMessage());
            return false;
        }
    }

    private String getNewTokenFromSecurityProvider() throws JsonProcessingException {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/x-www-form-urlencoded");

        String body = String.format("grant_type=password&client_id=%s&client_secret=%s&username=%s&password=%s",
                keycloakConfig.getClientId(), keycloakConfig.getKeycloakClientSecret(), keycloakConfig.getKeycloakUsername(), keycloakConfig.getKeycloakPassword());

        HttpEntity<String> request = new HttpEntity<>(body, headers);
        ResponseEntity<String> response = restTemplate.exchange(keycloakConfig.getKeycloakTokenUrl(), HttpMethod.POST, request, String.class);

        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(response.getBody());
        return root.path("access_token").asText();
    }

}
