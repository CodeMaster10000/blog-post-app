package com.scalefocus.mk.blog.api.core.security.rate_limit;

import lombok.Getter;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@Getter
public final class IpAddressesCache {
  private final Map<String, IpAddressInformation> ipAddresses = new HashMap<>();
}
