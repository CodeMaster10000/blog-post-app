package com.scalefocus.mk.blog.api.core.security.rate_limit;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
final class SessionRateHolder implements Serializable {

  private Instant firstRequest;
  Integer tokens;

}
