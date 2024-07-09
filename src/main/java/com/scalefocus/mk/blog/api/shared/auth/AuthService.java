package com.scalefocus.mk.blog.api.shared.auth;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

/**
 * Service for handling authentication and authorization related operations.
 * <p>
 * This service provides methods to retrieve the current authenticated user's username
 * and to validate that the current user matches a given username.
 * </p>
 */
@Component
public final class AuthService {

    /**
     * Retrieves the username of the currently authenticated user.
     * <p>
     * This method accesses the SecurityContextHolder to get the current authentication object,
     * extracts the JWT token, and retrieves the username from the JWT claims.
     * </p>
     *
     * @return the username of the currently authenticated user, or null if the authentication token is not a JWT token
     */
    public String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof JwtAuthenticationToken jwtAuthenticationToken) {
            Jwt jwt = (Jwt) jwtAuthenticationToken.getPrincipal();
            return jwt.getClaimAsString("preferred_username"); // assuming "preferred_username" is the claim used
        }
        return null;
    }

    /**
     * Validates that the provided username matches the username of the currently authenticated user.
     * <p>
     * This method retrieves the current authenticated user's username and compares it with the provided username.
     * If they do not match, it throws an UnsupportedOperationException.
     * </p>
     *
     * @param username the username to validate against the current authenticated user's username
     * @throws UnsupportedOperationException if the provided username does not match the current authenticated user's username
     */
    public void validateCurrentUsername(final String username) {
        String currentUsername = getCurrentUsername();
        if (!username.equals(currentUsername)) {
            throw new UnsupportedOperationException("Unable to perform operation with provided username");
        }
    }
}
