package com.kele.gongshibackend.controller;

import com.kele.gongshibackend.annotation.IgnoreAuth;
import com.kele.gongshibackend.common.Result;
import com.kele.gongshibackend.dto.LoginRequest;
import com.kele.gongshibackend.dto.LoginResponse;
import com.kele.gongshibackend.dto.PasswordChangeRequest;
import com.kele.gongshibackend.dto.RegisterRequest;
import com.kele.gongshibackend.dto.UserInfoResponse;
import com.kele.gongshibackend.service.AuthService;
import com.kele.gongshibackend.util.SecurityUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@Slf4j
@Tag(name = "认证管理", description = "用户登录、退出、权限管理接口")
@RestController
@RequestMapping("/auth")
public class AuthController {
    
    @Autowired
    private AuthService authService;
    
    @Operation(summary = "用户注册")
    @PostMapping("/register")
    @IgnoreAuth
    public Result<Void> register(@Valid @RequestBody RegisterRequest request) {
        authService.register(request);
        return Result.success("注册成功");
    }
    
    @Operation(summary = "用户登录")
    @PostMapping("/login")
    @IgnoreAuth
    public Result<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request);
        return Result.success("登录成功", response);
    }
    
    @Operation(summary = "用户退出")
    @PostMapping("/logout")
    public Result<Void> logout(HttpServletRequest request) {
        String token = extractToken(request);
        authService.logout(token);
        return Result.success("退出成功");
    }
    
    @Operation(summary = "获取当前用户信息")
    @GetMapping("/userinfo")
    public Result<UserInfoResponse> getUserInfo() {
        Long userId = SecurityUtil.getCurrentUserId();
        UserInfoResponse response = authService.getUserInfo(userId);
        return Result.success("查询成功", response);
    }
    
    @Operation(summary = "修改密码")
    @PutMapping("/password")
    public Result<Void> changePassword(@Valid @RequestBody PasswordChangeRequest request) {
        Long userId = SecurityUtil.getCurrentUserId();
        authService.changePassword(userId, request);
        return Result.success("密码修改成功");
    }
    
    @Operation(summary = "获取当前用户角色列表")
    @GetMapping("/roles")
    public Result<String[]> getRoles() {
        Long userId = SecurityUtil.getCurrentUserId();
        String[] roles = authService.getUserRoles(userId);
        return Result.success("查询成功", roles);
    }
    
    @Operation(summary = "检查用户是否有指定角色")
    @GetMapping("/hasRole/{roleCode}")
    public Result<Boolean> hasRole(
            @Parameter(description = "角色编码") @PathVariable String roleCode) {
        Long userId = SecurityUtil.getCurrentUserId();
        boolean hasRole = authService.hasRole(userId, roleCode);
        return Result.success("查询成功", hasRole);
    }
    
    private String extractToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }
}
