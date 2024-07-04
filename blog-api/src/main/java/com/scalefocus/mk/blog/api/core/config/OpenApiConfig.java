package com.scalefocus.mk.blog.api.core.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class OpenApiConfig {

    @Bean
    GroupedOpenApi customOpenApi() {
        return GroupedOpenApi.builder()
                .group("com.scalefocus.mk")
                .pathsToMatch("/api/**") // Replace with your custom API paths
                .build();
    }

    @Bean
    OpenAPI apiInfo() {
        return new OpenAPI().info(new Info().title("Blog Post Service API")
                .version("0.0.1")
                .license(new License().name("self-certified-license").url("")));
    }
}