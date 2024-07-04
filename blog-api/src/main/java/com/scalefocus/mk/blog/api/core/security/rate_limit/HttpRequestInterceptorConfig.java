package com.scalefocus.mk.blog.api.core.security.rate_limit;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.time.Duration;

@Configuration
class HttpRequestInterceptorConfig implements WebMvcConfigurer {

  private final IpAddressesCache cache;

  HttpRequestInterceptorConfig(IpAddressesCache cache) {
    this.cache = cache;
  }

  @Bean
  RateLimitFilter rateLimitFilterHttp() {
    Duration duration = Duration.ofSeconds(20);
    return new RateLimitFilter(20, duration);
  }

  @Bean
  IpAddressRateLimit rateLimitFilterForIp() {
    Duration window = Duration.ofSeconds(20);
    return new IpAddressRateLimit(cache, 20, window);
  }

  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    registry.addInterceptor(rateLimitFilterForIp()).addPathPatterns("/**");
    registry.addInterceptor(rateLimitFilterHttp())
        .addPathPatterns("/**")
        .addPathPatterns("/**");
  }
}
