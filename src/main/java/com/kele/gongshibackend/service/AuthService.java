package com.kele.gongshibackend.service;

import com.kele.gongshibackend.dto.LoginRequest;
import com.kele.gongshibackend.dto.LoginResponse;
import com.kele.gongshibackend.dto.PasswordChangeRequest;
import com.kele.gongshibackend.dto.RegisterRequest;
import com.kele.gongshibackend.dto.UserInfoResponse;

public interface AuthService {
    
    void register(RegisterRequest request);
    
    LoginResponse login(LoginRequest request);
    
    void logout(String token);
    
    UserInfoResponse getUserInfo(Long userId);
    
    String[] getUserRoles(Long userId);
    
    boolean hasRole(Long userId, String roleCode);
    
    void changePassword(Long userId, PasswordChangeRequest request);
    
    String encryptPassword(String rawPassword);
}
