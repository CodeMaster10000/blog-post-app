package com.scalefocus.mk.blog.api.blog.integration;

import com.scalefocus.mk.blog.api.core.config.EnvironmentInitializer;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Import;

@TestConfiguration
@Import(EnvironmentInitializer.class)
class EnvironmentConfig {
}
