package com.scalefocus.mk.blog.api.core.security.rate_limit;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.servlet.HandlerInterceptor;

import java.time.Duration;
import java.time.Instant;

/**
 * Interceptor for applying rate limiting to sessions.
 * <p>
 * This interceptor checks the rate limits for sessions of incoming requests,
 * blocking requests that exceed the allowed rate within a specified time window.
 * </p>
 */
final class RateLimitFilter implements HandlerInterceptor {

  private static final String SESSION_BUCKET_KEY = "session";
  private final Duration duration;
  private final int requests;

  RateLimitFilter(int requests, Duration duration) {
    this.duration = duration;
    this.requests = requests;
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
    HttpSession session = request.getSession();
    return validateSessionBucket(
            response,
            session,
            (SessionRateHolder) session.getAttribute(SESSION_BUCKET_KEY),
            Instant.now());
  }

  /**
   * Validates the session rate bucket and checks if it exceeds the rate limit.
   *
   * @param response          the HTTP response
   * @param session           the HTTP session
   * @param sessionRateHolder the session rate holder
   * @param now               the current time
   * @return true if the session is within the allowed rate limit, false otherwise
   */
  private boolean validateSessionBucket(HttpServletResponse response, HttpSession session,
                                        SessionRateHolder sessionRateHolder, Instant now) {
    createNewBucketIfNull(session, now, sessionRateHolder);
    if (validateTokenConsumption(session)) return true;
    response.setStatus(429);
    return false;
  }

  /**
   * Creates a new session rate bucket if it is null or has expired.
   *
   * @param session           the HTTP session
   * @param now               the current time
   * @param sessionRateHolder the session rate holder
   */
  private void createNewBucketIfNull(HttpSession session, Instant now, SessionRateHolder sessionRateHolder) {
    if (sessionRateHolder == null || Duration.between(sessionRateHolder.getFirstRequest(), now).compareTo(duration) >= 0) {
      session.setAttribute(
              SESSION_BUCKET_KEY,
              new SessionRateHolder(now, requests));
    }
  }

  /**
   * Validates the consumption of tokens in the session rate bucket.
   *
   * @param session the HTTP session
   * @return true if there are tokens available, false otherwise
   */
  private boolean validateTokenConsumption(HttpSession session) {
    SessionRateHolder sessionRateHolder = (SessionRateHolder) session.getAttribute(SESSION_BUCKET_KEY);
    return checkTokenPresentAndRemoveOne(sessionRateHolder);
  }

  /**
   * Checks if a token is present in the session rate holder and removes one if present.
   *
   * @param sessionRateHolder the session rate holder
   * @return true if a token was present and removed, false otherwise
   */
  private boolean checkTokenPresentAndRemoveOne(SessionRateHolder sessionRateHolder) {
    Integer tokens = sessionRateHolder.getTokens();
    if (tokens > 0) {
      sessionRateHolder.setTokens(tokens - 1);
      return true;
    }
    return false;
  }
}
