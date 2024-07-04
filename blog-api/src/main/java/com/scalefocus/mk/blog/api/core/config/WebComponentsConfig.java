package com.scalefocus.mk.blog.api.core.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
class WebComponentsConfig {

    @Bean
    RestTemplate restTemplate() {
        return new RestTemplate();
    }

}
