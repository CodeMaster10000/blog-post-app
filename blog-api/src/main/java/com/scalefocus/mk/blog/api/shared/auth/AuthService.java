package com.scalefocus.mk.blog.api.shared.auth;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

@Component
public final class AuthService {

    public String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof JwtAuthenticationToken jwtAuthenticationToken) {
            Jwt jwt = (Jwt) jwtAuthenticationToken.getPrincipal();
            return jwt.getClaimAsString("preferred_username"); // assuming "preferred_username" is the claim used
        }
        return null;
    }

    public void validateCurrentUsername(final String username) {
        String currentUsername = getCurrentUsername();
       if (!username.equals(currentUsername)) {
           throw new UnsupportedOperationException("Unable to perform operation with provided username");
       }
    }

}
