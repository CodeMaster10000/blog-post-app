package com.scalefocus.mk.blog.api.core.security.rate_limit;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.servlet.HandlerInterceptor;

import java.time.Duration;
import java.time.Instant;

final class RateLimitFilter implements HandlerInterceptor {

  private static final String SESSION_BUCKET_KEY = "session";
  private final Duration duration;
  private final int requests;

  RateLimitFilter(int requests, Duration duration) {
    this.duration = duration;
    this.requests = requests;
  }

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
    HttpSession session = request.getSession();
    return validateSessionBucket(
        response,
        session,
        (SessionRateHolder) session.getAttribute(SESSION_BUCKET_KEY),
        Instant.now());
  }

  private boolean validateSessionBucket(HttpServletResponse response, HttpSession session,
      SessionRateHolder testSessionBucket, Instant now) {
    createNewBucketIfNull(session, now, testSessionBucket);
    if (validateTokenConsumption(session)) return true;
    response.setStatus(429);
    return false;
  }

  private void createNewBucketIfNull(HttpSession session, Instant now, SessionRateHolder testSessionBucket) {
    if (testSessionBucket == null || Duration.between(testSessionBucket.getFirstRequest(), now).compareTo(duration) >= 0) {
      session.setAttribute(
          SESSION_BUCKET_KEY,
          new SessionRateHolder(now, requests));
    }
  }

  private boolean validateTokenConsumption(HttpSession session) {
    SessionRateHolder sessionRateHolder = (SessionRateHolder) session.getAttribute(SESSION_BUCKET_KEY);
    return checkTokenPresentAndRemoveOne(sessionRateHolder);
  }

  private boolean checkTokenPresentAndRemoveOne(SessionRateHolder sessionRateHolder) {
    Integer tokens = sessionRateHolder.getTokens();
    if (tokens > 0) {
      sessionRateHolder.setTokens(tokens - 1);
      return true;
    }
    return false;
  }

}
