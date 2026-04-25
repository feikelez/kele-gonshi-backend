package com.kele.gongshibackend.util;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;

public class SecurityUtil {
    
    private static final String USER_ID_ATTRIBUTE = "currentUserId";
    private static final String USERNAME_ATTRIBUTE = "currentUsername";
    
    public static Long getCurrentUserId() {
        HttpServletRequest request = getRequest();
        Object userId = request.getAttribute(USER_ID_ATTRIBUTE);
        return userId != null ? (Long) userId : null;
    }
    
    public static String getCurrentUsername() {
        HttpServletRequest request = getRequest();
        Object username = request.getAttribute(USERNAME_ATTRIBUTE);
        return username != null ? (String) username : null;
    }
    
    public static void setCurrentUser(Long userId, String username) {
        HttpServletRequest request = getRequest();
        request.setAttribute(USER_ID_ATTRIBUTE, userId);
        request.setAttribute(USERNAME_ATTRIBUTE, username);
    }
    
    private static HttpServletRequest getRequest() {
        ServletRequestAttributes attributes = 
            (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return attributes != null ? attributes.getRequest() : null;
    }
}
