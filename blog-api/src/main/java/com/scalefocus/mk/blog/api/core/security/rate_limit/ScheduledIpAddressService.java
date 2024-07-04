package com.scalefocus.mk.blog.api.core.security.rate_limit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
final class ScheduledIpAddressService {

  private final IpAddressesCache cache;

  private static final Logger logger = LoggerFactory.getLogger(ScheduledIpAddressService.class);

  ScheduledIpAddressService(IpAddressesCache cache) {
    this.cache = cache;
  }

  @Scheduled(fixedRate = 45 * 60 * 1000)
  public void unbanIpAddresses() {
    cache.getIpAddresses().clear();
    String message = "Cleared cache for ip addresses";
    logger.info(message);
  }

}
