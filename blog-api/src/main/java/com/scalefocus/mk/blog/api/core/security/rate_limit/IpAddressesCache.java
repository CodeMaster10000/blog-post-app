package com.scalefocus.mk.blog.api.core.security.rate_limit;

import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Cache for storing IP addresses and their associated rate limit information.
 * <p>
 * This class provides a map to store and manage the rate limits for different IP addresses.
 * It uses a HashMap to store the mapping between IP addresses and their corresponding
 * {@link IpAddressInformation} objects.
 * </p>
 */
@Component
@Getter
final class IpAddressesCache {

  private final Map<String, IpAddressInformation> ipAddresses = new HashMap<>();

}
