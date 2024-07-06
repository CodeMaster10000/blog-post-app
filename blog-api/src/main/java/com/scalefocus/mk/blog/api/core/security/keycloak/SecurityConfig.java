package com.scalefocus.mk.blog.api.core.security.keycloak;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.user.OidcUserAuthority;
import org.springframework.security.oauth2.core.user.OAuth2UserAuthority;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.session.RegisterSessionAuthenticationStrategy;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.security.web.session.HttpSessionEventPublisher;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Security configuration class for Keycloak integration.
 * <p>
 * This class configures security settings for the application, including session management,
 * CORS settings, and authorization rules for different endpoints.
 * </p>
 */
@Configuration
@EnableWebSecurity
class SecurityConfig {

  private static final String REALM_ACCESS_CLAIM = "realm_access";
  private static final String ROLES_CLAIM = "roles";

  private final OAuth2AuthorizedClientService authorizedClientService;
  private final KeycloakLogoutHandler keycloakLogoutHandler;

  public SecurityConfig(OAuth2AuthorizedClientService authorizedClientService, KeycloakLogoutHandler keycloakLogoutHandler) {
    this.authorizedClientService = authorizedClientService;
    this.keycloakLogoutHandler = keycloakLogoutHandler;
  }

  /**
   * Creates a bean for managing session registry.
   *
   * @return a SessionRegistry instance
   */
  @Bean
  public SessionRegistry sessionRegistry() {
    return new SessionRegistryImpl();
  }

  /**
   * Creates a bean for session authentication strategy.
   *
   * @return a SessionAuthenticationStrategy instance
   */
  @Bean
  protected SessionAuthenticationStrategy sessionAuthenticationStrategy() {
    return new RegisterSessionAuthenticationStrategy(sessionRegistry());
  }

  /**
   * Creates a bean for handling HTTP session events.
   *
   * @return an HttpSessionEventPublisher instance
   */
  @Bean
  public HttpSessionEventPublisher httpSessionEventPublisher() {
    return new HttpSessionEventPublisher();
  }

  /**
   * Configures the security filter chain.
   * <p>
   * This method sets up the security filters, including OAuth2 login and logout handling,
   * session management, and authorization rules for different HTTP methods and paths.
   * </p>
   *
   * @param http the HttpSecurity instance to configure
   * @return a SecurityFilterChain instance
   * @throws Exception if an error occurs during configuration
   */
  @Bean
  public SecurityFilterChain resourceServerFilterChain(HttpSecurity http) throws Exception {
    http.authorizeHttpRequests(auth -> auth
                    .requestMatchers(new AntPathRequestMatcher("/", HttpMethod.OPTIONS.name())).permitAll()
                    .requestMatchers(new AntPathRequestMatcher("/", HttpMethod.GET.name())).permitAll()
                    .anyRequest().authenticated())
            .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()))
            .oauth2Login(oauth2 -> oauth2
                    .successHandler((request, response, authentication) -> {
                      OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
                      OAuth2AuthorizedClient client = authorizedClientService.loadAuthorizedClient(
                              oauthToken.getAuthorizedClientRegistrationId(), oauthToken.getName());
                      if (client != null) response.sendRedirect("/swagger-ui/index.html");
                    })
            )
            .logout(logout -> logout
                    .addLogoutHandler(keycloakLogoutHandler)
                    .logoutSuccessUrl("/"));

    return http.build();
  }

  /**
   * Creates a bean for mapping user authorities.
   * <p>
   * This method maps roles from the Keycloak access token to Spring Security authorities.
   * </p>
   *
   * @return a GrantedAuthoritiesMapper instance
   */
  @Bean
  public GrantedAuthoritiesMapper userAuthoritiesMapper() {
    return authorities -> {
      Set<GrantedAuthority> mappedAuthorities = new HashSet<>();
      authorities.forEach(authority -> {
        if (authority instanceof OidcUserAuthority oidcUserAuthority) {
          Map<String, Object> userInfo = oidcUserAuthority.getUserInfo().getClaims();
          extractAndMapRoles(userInfo, mappedAuthorities);
        } else if (authority instanceof OAuth2UserAuthority oauth2UserAuthority) {
          Map<String, Object> userAttributes = oauth2UserAuthority.getAttributes();
          extractAndMapRoles(userAttributes, mappedAuthorities);
        }
      });
      return mappedAuthorities;
    };
  }

  @SuppressWarnings("unchecked")
  private void extractAndMapRoles(Map<String, Object> claims, Set<GrantedAuthority> mappedAuthorities) {
    if (claims.containsKey(REALM_ACCESS_CLAIM)) {
      Map<String, Object> realmAccess = (Map<String, Object>) claims.get(REALM_ACCESS_CLAIM);
      Collection<String> roles = (Collection<String>) realmAccess.get(ROLES_CLAIM);
      roles.forEach(role -> mappedAuthorities.add(new SimpleGrantedAuthority("ROLE_" + role)));
    }
  }
}
