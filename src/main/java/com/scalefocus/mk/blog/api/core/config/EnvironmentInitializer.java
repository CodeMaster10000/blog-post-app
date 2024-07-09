package com.scalefocus.mk.blog.api.core.config;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

import java.util.HashMap;
import java.util.Map;

public final class EnvironmentInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        ConfigurableEnvironment environment = applicationContext.getEnvironment();
        Dotenv dotenv = Dotenv.load();
        Map<String, Object> envMap = new HashMap<>();
        dotenv.entries().forEach(entry -> envMap.put(entry.getKey(), entry.getValue()));
        environment.getPropertySources().addFirst(new MapPropertySource("dotenv", envMap));
    }
}