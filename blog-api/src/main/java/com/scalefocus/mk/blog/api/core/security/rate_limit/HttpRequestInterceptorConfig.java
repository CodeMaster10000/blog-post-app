package com.scalefocus.mk.blog.api.core.security.rate_limit;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.time.Duration;

/**
 * Configuration class for registering HTTP request interceptors.
 * <p>
 * This class implements WebMvcConfigurer to register custom interceptors for handling
 * HTTP requests and applying rate limiting.
 * </p>
 */
@Configuration
class HttpRequestInterceptorConfig implements WebMvcConfigurer {

  private final IpAddressesCache cache;

  HttpRequestInterceptorConfig(IpAddressesCache cache) {
    this.cache = cache;
  }

  /**
   * Bean definition for the HTTP rate limit filter.
   * <p>
   * This bean creates a RateLimitFilter with a limit of 20 requests and a window of 20 seconds.
   * </p>
   *
   * @return the RateLimitFilter for HTTP requests
   */
  @Bean
  RateLimitFilter rateLimitFilterHttp() {
    Duration duration = Duration.ofSeconds(20);
    return new RateLimitFilter(20, duration);
  }

  /**
   * Bean definition for the IP address rate limit filter.
   * <p>
   * This bean creates an IpAddressRateLimit with a limit of 20 requests and a window of 20 seconds,
   * using the provided IpAddressesCache.
   * </p>
   *
   * @return the IpAddressRateLimit for IP address-based rate limiting
   */
  @Bean
  IpAddressRateLimit rateLimitFilterForIp() {
    Duration window = Duration.ofSeconds(20);
    return new IpAddressRateLimit(cache, 20, window);
  }

  /**
   * Adds the custom interceptors to the interceptor registry.
   * <p>
   * This method registers both the IP address rate limit filter and the HTTP rate limit filter
   * to apply rate limiting on all incoming requests.
   * </p>
   *
   * @param registry the interceptor registry
   */
  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    registry.addInterceptor(rateLimitFilterForIp()).addPathPatterns("/**");
    registry.addInterceptor(rateLimitFilterHttp())
            .addPathPatterns("/**")
            .addPathPatterns("/**");
  }
}
