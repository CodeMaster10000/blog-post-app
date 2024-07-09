package com.scalefocus.mk.blog.api.core.security.rate_limit;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.Instant;

/**
 * Class representing information about an IP address related to rate limiting.
 * <p>
 * This class holds details about the first request time, request count, and block status
 * for an IP address. It is used to manage and monitor the rate limits applied to different
 * IP addresses.
 * </p>
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
final class IpAddressInformation implements Serializable {

  private Instant firstRequest;
  private Integer requestCount;
  private boolean blocked;

}
