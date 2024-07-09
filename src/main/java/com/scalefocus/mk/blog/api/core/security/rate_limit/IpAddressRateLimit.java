package com.scalefocus.mk.blog.api.core.security.rate_limit;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;

import java.time.Duration;
import java.time.Instant;

/**
 * Interceptor for applying rate limiting based on IP addresses.
 * <p>
 * This interceptor checks the rate limits for the IP addresses of incoming requests,
 * blocking requests that exceed the allowed rate within a specified time window.
 * </p>
 */
final class IpAddressRateLimit implements HandlerInterceptor {

  private final IpAddressesCache cache;
  private final int maxRequests;
  private final Duration window;

  public IpAddressRateLimit(IpAddressesCache cache, int maxRequests, Duration window) {
    this.cache = cache;
    this.maxRequests = maxRequests;
    this.window = window;
  }

  /**
   * Pre-handle method to check and enforce rate limits before processing the request.
   *
   * @param request  the incoming HTTP request
   * @param response the HTTP response
   * @param handler  the handler (or controller) to execute
   * @return true if the request is allowed, false otherwise
   */
  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
    String ipAddress = request.getRemoteAddr();
    IpAddressInformation ipAddressInformation = cache.getIpAddresses().get(ipAddress);
    return validateIpAddress(response, ipAddress, cache, ipAddressInformation);
  }

  /**
   * Validates the IP address and checks if it exceeds the rate limit.
   *
   * @param response             the HTTP response
   * @param ipAddress            the IP address to validate
   * @param cache                the IP addresses cache
   * @param ipAddressInformation the information associated with the IP address
   * @return true if the IP address is within the allowed rate limit, false otherwise
   */
  private boolean validateIpAddress(HttpServletResponse response, String ipAddress, IpAddressesCache cache, IpAddressInformation ipAddressInformation) {
    if (ipAddressInformation == null) {
      return createNewIpAddressInput(ipAddress, cache);
    }
    if (ipAddressInformation.isBlocked()) {
      return tooManyRequestsResponse(response);
    }
    if (ipAddressExpired(ipAddressInformation, cache, ipAddress)) {
      return blockIpAddress(response, ipAddress, cache, ipAddressInformation);
    }
    return updateIpAddress(ipAddress, cache, ipAddressInformation);
  }

  /**
   * Updates the request count for the given IP address.
   *
   * @param ipAddress            the IP address to update
   * @param cache                the IP addresses cache
   * @param ipAddressInformation the information associated with the IP address
   * @return true after updating the IP address information
   */
  private boolean updateIpAddress(String ipAddress, IpAddressesCache cache, IpAddressInformation ipAddressInformation) {
    ipAddressInformation.setRequestCount(ipAddressInformation.getRequestCount() + 1);
    cache.getIpAddresses().put(ipAddress, ipAddressInformation);
    return true;
  }

  /**
   * Blocks the given IP address by setting its blocked status to true.
   *
   * @param response             the HTTP response
   * @param ipAddress            the IP address to block
   * @param cache                the IP addresses cache
   * @param ipAddressInformation the information associated with the IP address
   * @return false to indicate the request is blocked
   */
  private boolean blockIpAddress(HttpServletResponse response, String ipAddress, IpAddressesCache cache, IpAddressInformation ipAddressInformation) {
    ipAddressInformation.setBlocked(true);
    cache.getIpAddresses().put(ipAddress, ipAddressInformation);
    return tooManyRequestsResponse(response);
  }

  /**
   * Checks if the IP address has exceeded the request limit within the specified time window.
   *
   * @param ipAddressInformation the information associated with the IP address
   * @param cache                the IP addresses cache
   * @param ipAddress            the IP address to check
   * @return true if the IP address has exceeded the limit, false otherwise
   */
  private boolean ipAddressExpired(IpAddressInformation ipAddressInformation, IpAddressesCache cache, String ipAddress) {
    if (ipAddressInformation.getRequestCount() >= maxRequests) {
      if (Duration.between(ipAddressInformation.getFirstRequest(), Instant.now()).compareTo(window) < 0) {
        return true;
      } else {
        ipAddressInformation.setRequestCount(0);
        ipAddressInformation.setFirstRequest(Instant.now());
        cache.getIpAddresses().put(ipAddress, ipAddressInformation);
        return false;
      }
    }
    return false;
  }

  /**
   * Sends a 429 Too Many Requests response.
   *
   * @param response the HTTP response
   * @return false to indicate the request is blocked
   */
  private boolean tooManyRequestsResponse(HttpServletResponse response) {
    response.setStatus(429);
    return false;
  }

  /**
   * Creates a new IP address entry in the cache.
   *
   * @param ipAddress the IP address to add
   * @param cache     the IP addresses cache
   * @return true after creating the new IP address entry
   */
  private boolean createNewIpAddressInput(String ipAddress, IpAddressesCache cache) {
    IpAddressInformation ipAddressInformation = new IpAddressInformation(Instant.now(), 0, false);
    cache.getIpAddresses().put(ipAddress, ipAddressInformation);
    return true;
  }
}
