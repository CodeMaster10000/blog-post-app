package com.scalefocus.mk.blog.api.core.security.rate_limit;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.Instant;

/**
 * Class representing the rate limit information for a session.
 * <p>
 * This class holds details about the first request time and the number of remaining tokens
 * for a session. It is used to manage and monitor the rate limits applied to different sessions.
 * </p>
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
final class SessionRateHolder implements Serializable {

  private Instant firstRequest;
  private Integer tokens;
}
