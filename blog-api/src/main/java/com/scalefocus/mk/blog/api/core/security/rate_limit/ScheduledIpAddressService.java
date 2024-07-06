package com.scalefocus.mk.blog.api.core.security.rate_limit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/**
 * Scheduled service for managing IP address rate limits.
 * <p>
 * This service provides a scheduled task to periodically clear the IP addresses cache,
 * effectively unblocking all previously blocked IP addresses.
 * </p>
 */
@Service
final class ScheduledIpAddressService {

  private final IpAddressesCache cache;
  private static final Logger logger = LoggerFactory.getLogger(ScheduledIpAddressService.class);

  ScheduledIpAddressService(IpAddressesCache cache) {
    this.cache = cache;
  }

  /**
   * Scheduled task to clear the IP addresses cache.
   * <p>
   * This method runs at a fixed rate of 45 minutes, clearing all entries in the cache
   * and logging the action.
   * </p>
   */
  @Scheduled(fixedRate = 45 * 60 * 1000) // 45 minutes
  public void unbanIpAddresses() {
    cache.getIpAddresses().clear();
    String message = "Cleared cache for IP addresses";
    logger.info(message);
  }
}
