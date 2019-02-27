package com.abcapps.utils;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class AuthUtils {
    public static String getLoggedInUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return ((org.springframework.security.core.userdetails.User) authentication.getPrincipal()).getUsername();
    }
}
