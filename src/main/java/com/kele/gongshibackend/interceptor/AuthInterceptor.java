package com.kele.gongshibackend.interceptor;

import com.kele.gongshibackend.annotation.IgnoreAuth;
import com.kele.gongshibackend.exception.AuthenticationException;
import com.kele.gongshibackend.service.TokenBlacklistService;
import com.kele.gongshibackend.util.JwtUtil;
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
public class AuthInterceptor implements HandlerInterceptor {
    
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @Autowired
    private TokenBlacklistService tokenBlacklistService;
    
    @Override
    public boolean preHandle(HttpServletRequest request, 
                           HttpServletResponse response, 
                           Object handler) throws Exception {
        
        // 检查是否需要忽略认证
        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            IgnoreAuth ignoreAuth = handlerMethod.getMethodAnnotation(IgnoreAuth.class);
            if (ignoreAuth != null) {
                return true;
            }
            
            // 检查类级别注解
            ignoreAuth = handlerMethod.getBeanType().getAnnotation(IgnoreAuth.class);
            if (ignoreAuth != null) {
                return true;
            }
        }
        
        // 获取Token
        String token = extractToken(request);
        if (token == null) {
            throw new AuthenticationException("未登录或Token已过期");
        }
        
        // 验证Token
        if (!jwtUtil.validateToken(token)) {
            throw new AuthenticationException("Token无效或已过期");
        }
        
        // 检查Token是否在黑名单
        if (tokenBlacklistService.isInBlacklist(token)) {
            throw new AuthenticationException("Token已失效，请重新登录");
        }
        
        // 设置当前用户信息
        Long userId = jwtUtil.getUserIdFromToken(token);
        String username = jwtUtil.getUsernameFromToken(token);
        SecurityUtil.setCurrentUser(userId, username);
        
        return true;
    }
    
    private String extractToken(HttpServletRequest request) {
        String authHeader = request.getHeader(AUTHORIZATION_HEADER);
        if (authHeader != null && authHeader.startsWith(BEARER_PREFIX)) {
            return authHeader.substring(BEARER_PREFIX.length());
        }
        return null;
    }
}
