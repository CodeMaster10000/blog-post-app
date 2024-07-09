package com.scalefocus.mk.blog.api.core.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class OpenApiConfig {

    /**
     * Configures a custom OpenAPI group.
     * <p>
     * This method creates a {@link GroupedOpenApi} bean that matches the specified API paths.
     * </p>
     *
     * @return a configured {@link GroupedOpenApi} instance
     */
    @Bean
    GroupedOpenApi customOpenApi() {
        return GroupedOpenApi.builder()
                .group("com.scalefocus.mk")
                .pathsToMatch("/api/**") // Replace with your custom API paths
                .build();
    }

    /**
     * Configures basic API information.
     * <p>
     * This method creates an {@link OpenAPI} bean with basic information about the API,
     * such as the title, version, and license.
     * </p>
     *
     * @return a configured {@link OpenAPI} instance
     */
    @Bean
    OpenAPI apiInfo() {
        return new OpenAPI().info(new Info()
                .title("Blog Post Service API")
                .version("0.0.1")
                .license(new License().name("self-certified-license").url("")));
    }
}