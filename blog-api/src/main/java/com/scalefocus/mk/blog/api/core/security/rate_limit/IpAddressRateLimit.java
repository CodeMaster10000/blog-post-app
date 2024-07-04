package com.scalefocus.mk.blog.api.core.security.rate_limit;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;

import java.time.Duration;
import java.time.Instant;

final class IpAddressRateLimit implements HandlerInterceptor {

  private final IpAddressesCache cache;
  private final int maxRequests;
  private final Duration window;

  public IpAddressRateLimit(IpAddressesCache cache, int maxRequests, Duration window) {
    this.cache = cache;
    this.maxRequests = maxRequests;
    this.window = window;
  }

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
    String ipAddress = request.getRemoteAddr();
    IpAddressInformation ipAddressInformation = cache.getIpAddresses().get(ipAddress);
    return validateIpAddress(response, ipAddress, cache, ipAddressInformation);
  }

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

  private boolean updateIpAddress(String ipAddress, IpAddressesCache cache, IpAddressInformation ipAddressInformation) {
    ipAddressInformation.setRequestCount(ipAddressInformation.getRequestCount() + 1);
    cache.getIpAddresses().put(ipAddress, ipAddressInformation);
    return true;
  }

  private boolean blockIpAddress(HttpServletResponse response, String ipAddress, IpAddressesCache cache, IpAddressInformation ipAddressInformation) {
    ipAddressInformation.setBlocked(true);
    cache.getIpAddresses().put(ipAddress, ipAddressInformation);
    return tooManyRequestsResponse(response);
  }

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
    } return false;
  }

  private boolean tooManyRequestsResponse(HttpServletResponse response) {
    response.setStatus(429);
    return false;
  }

  private boolean createNewIpAddressInput(String ipAddress, IpAddressesCache cache) {
    IpAddressInformation ipAddressInformation = new IpAddressInformation(Instant.now(), 0, false);
    cache.getIpAddresses().put(ipAddress, ipAddressInformation);
    return true;
  }
}
