package com.projectmanagement.multitenantprojectmanagement.auth0.utils;

import java.util.UUID;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import com.projectmanagement.multitenantprojectmanagement.auth0.Auth0Config;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class JWTUtils {

    public static String getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof JwtAuthenticationToken jwtAuth) {
            String userId = jwtAuth.getToken().getSubject();
            return userId;
        }
        throw new RuntimeException("Invalid token");
    }

    public static String getAuth0OrgId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth instanceof JwtAuthenticationToken jwtAuth) {
            return jwtAuth.getToken().getClaim("org_id");
        }
        throw new RuntimeException("Invalid token");
    }
}
