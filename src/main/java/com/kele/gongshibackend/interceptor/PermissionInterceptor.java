package com.kele.gongshibackend.interceptor;

import com.kele.gongshibackend.annotation.RequireRole;
import com.kele.gongshibackend.exception.AuthenticationException;
import com.kele.gongshibackend.service.AuthService;
import com.kele.gongshibackend.util.SecurityUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Slf4j
@Component
public class PermissionInterceptor implements HandlerInterceptor {
    
    @Autowired
    private AuthService authService;
    
    @Override
    public boolean preHandle(HttpServletRequest request, 
                           HttpServletResponse response, 
                           Object handler) throws Exception {
        
        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            RequireRole requireRole = handlerMethod.getMethodAnnotation(RequireRole.class);
            
            if (requireRole != null) {
                Long userId = SecurityUtil.getCurrentUserId();
                if (userId == null) {
                    throw new AuthenticationException("未登录");
                }
                
                String[] requiredRoles = requireRole.value();
                boolean hasPermission = false;
                
                for (String roleCode : requiredRoles) {
                    if (authService.hasRole(userId, roleCode)) {
                        hasPermission = true;
                        break;
                    }
                }
                
                if (!hasPermission) {
                    throw new AuthenticationException("权限不足，需要角色: " + 
                            String.join(", ", requiredRoles));
                }
            }
        }
        
        return true;
    }
}
