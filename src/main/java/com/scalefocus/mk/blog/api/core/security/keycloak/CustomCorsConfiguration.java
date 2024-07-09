package com.scalefocus.mk.blog.api.core.security.keycloak;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configuration class for setting up CORS (Cross-Origin Resource Sharing) in the application.
 * <p>
 * This class extends {@link CorsConfiguration} and implements {@link WebMvcConfigurer} to configure
 * CORS mappings, allowing specific HTTP methods, headers, and origins.
 * </p>
 */
@Configuration
class CustomCorsConfiguration extends CorsConfiguration implements WebMvcConfigurer {

  /**
   * Configures path matching options.
   *
   * @param configurer the PathMatchConfigurer to configure path matching options
   */
  @Override
  public void configurePathMatch(PathMatchConfigurer configurer) {
    WebMvcConfigurer.super.configurePathMatch(configurer);
  }

  /**
   * Adds CORS mappings to the application.
   * <p>
   * This method configures CORS settings such as allowed origins, methods, headers, and other options
   * for requests matching the specified paths.
   * </p>
   *
   * @param registry the CorsRegistry to which the CORS configurations are added
   */
  @Override
  public void addCorsMappings(CorsRegistry registry) {
    registry.addMapping("/**")
            .allowedOriginPatterns("*")
            .allowedMethods("GET", "POST", "DELETE", "PUT", "OPTIONS")
            .allowedHeaders("*")
            .exposedHeaders("Authorization")
            .allowCredentials(true)
            .maxAge(7200);
  }
}
