package com.store.application.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service("securityService")
public class SecurityService {
    public boolean hasPermission(String action) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        String permission = action.toUpperCase() + "_PERM";
        return authentication.getAuthorities().stream()
                .anyMatch(g -> g.getAuthority().equals(permission));
    }
}
