package com.scalefocus.mk.blog.api.core.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * Configuration class for web components.
 * <p>
 * This class provides configuration for web-related beans, such as {@link RestTemplate},
 * which is used for making RESTful web service calls.
 * </p>
 */
@Configuration
class WebComponentsConfig {

    /**
     * Creates a {@link RestTemplate} bean.
     * <p>
     * This method configures and returns a {@link RestTemplate} instance that can be used
     * for making HTTP requests to external services.
     * </p>
     *
     * @return a configured {@link RestTemplate} instance
     */
    @Bean
    RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
