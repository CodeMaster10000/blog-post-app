package com.scalefocus.mk.blog.api.core.security.keycloak;

/**
 * Exception thrown when a security resource operation fails.
 * <p>
 * This custom runtime exception is used to indicate errors related to security resource operations,
 * such as creating or deleting users and roles in Keycloak.
 * </p>
 */
final class SecurityResourceException extends RuntimeException {

  /**
   * Constructs a new SecurityResourceException with the specified detail message.
   *
   * @param message the detail message explaining the reason for the exception
   */
  SecurityResourceException(String message) {
    super(message);
  }

}

