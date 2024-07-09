package com.scalefocus.mk.blog.api.core.security.keycloak;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * Handler for managing Keycloak logout.
 * <p>
 * This class implements {@link LogoutHandler} to propagate logout requests to Keycloak.
 * It uses a {@link RestTemplate} to call the Keycloak logout endpoint.
 * </p>
 */
@Component
public final class KeycloakLogoutHandler implements LogoutHandler {

  private static final Logger logger = LoggerFactory.getLogger(KeycloakLogoutHandler.class);
  private final RestTemplate restTemplate;

  public KeycloakLogoutHandler(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

  /**
   * Handles the logout process.
   * <p>
   * This method is called when a user logs out. It retrieves the OIDC user from the authentication
   * object and calls the {@link #logoutFromKeycloak(OidcUser)} method to propagate the logout to Keycloak.
   * </p>
   *
   * @param request  the HTTP request
   * @param response the HTTP response
   * @param auth     the authentication object
   */
  @Override
  public void logout(HttpServletRequest request, HttpServletResponse response, Authentication auth) {
    logoutFromKeycloak((OidcUser) auth.getPrincipal());
  }

  /**
   * Propagates the logout request to Keycloak.
   * <p>
   * This method constructs the Keycloak logout endpoint URL and sends a GET request to it using
   * the user's ID token. It logs the result of the logout operation.
   * </p>
   *
   * @param user the OIDC user
   */
  private void logoutFromKeycloak(OidcUser user) {
    String endSessionEndpoint = user.getIssuer() + "/protocol/openid-connect/logout";
    UriComponentsBuilder builder = UriComponentsBuilder
            .fromUriString(endSessionEndpoint)
            .queryParam("id_token_hint", user.getIdToken().getTokenValue());

    ResponseEntity<String> logoutResponse = restTemplate.getForEntity(builder.toUriString(), String.class);
    if (logoutResponse.getStatusCode().is2xxSuccessful()) {
      logger.info("Successfully logged out from Keycloak");
    } else {
      logger.error("Could not propagate logout to Keycloak");
    }
  }
}
