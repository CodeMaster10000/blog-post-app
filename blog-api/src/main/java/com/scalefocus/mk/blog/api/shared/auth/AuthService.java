package com.scalefocus.mk.blog.api.shared.auth;

import com.scalefocus.mk.blog.api.shared.exceptions.IllegalOperationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

public final class AuthService {

    private AuthService() {
        throw new AssertionError("No instance of this class");
    }

    public static String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof JwtAuthenticationToken jwtAuthenticationToken) {
            Jwt jwt = (Jwt) jwtAuthenticationToken.getPrincipal();
            return jwt.getClaimAsString("preferred_username"); // assuming "preferred_username" is the claim used
        }
        return null;
    }

    public static void validateCurrentUsername(final String username) {
        String currentUsername = getCurrentUsername();
       if (!username.equals(currentUsername)) {
           throw new IllegalOperationException("Unable to perform operation with provided username");
       }
    }

}
