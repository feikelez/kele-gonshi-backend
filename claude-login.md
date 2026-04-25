# 设计登录认证实现方案

Perfect! Now I have a comprehensive understanding of the codebase. Let me design a complete implementation plan for the authentication, logout, and role-based access control system.

# 完整实现方案：登录、退出和角色权限识别

## 一、项目现状分析

基于代码探索，项目具有以下特点：
- **技术栈**：Spring Boot 3.2.6 + MyBatis-Plus 3.5.7 + MySQL
- **代码风格**：统一使用 Result 返回、Swagger 注解、Lombok
- **包结构**：entity、mapper、service、controller、common
- **现有表**：sys_user、sys_role、sys_user_role
- **密码存储**：当前为明文（需要迁移到 BCrypt）
- **认证状态**：无认证机制，需要从零构建

## 二、需要添加的 Maven 依赖

在 `pom.xml` 中添加以下依赖：

```xml
<!-- JWT 依赖 -->
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-api</artifactId>
    <version>0.11.5</version>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-impl</artifactId>
    <version>0.11.5</version>
    <scope>runtime</scope>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-jackson</artifactId>
    <version>0.11.5</version>
    <scope>runtime</scope>
</dependency>

<!-- 参数校验 -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-validation</artifactId>
</dependency>

<!-- BCrypt 密码加密（Spring Security 已包含） -->
<dependency>
    <groupId>org.springframework.security</groupId>
    <artifactId>spring-security-crypto</artifactId>
</dependency>
```

## 三、数据库表结构变更SQL

### 1. 新增 Token 黑名单表

```sql
-- Token 黑名单表
CREATE TABLE sys_token_blacklist
(
    id          BIGINT PRIMARY KEY AUTO_INCREMENT,
    token       VARCHAR(500) NOT NULL UNIQUE,
    user_id     BIGINT NOT NULL,
    expire_time DATETIME NOT NULL,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_user_id (user_id),
    INDEX idx_expire_time (expire_time)
);
```

### 2. 密码字段长度调整（确保能存储 BCrypt 哈希值）

```sql
-- 确保 password 字段长度足够（BCrypt 哈希为 60 字符）
ALTER TABLE sys_user MODIFY COLUMN password VARCHAR(255) NOT NULL;
```

### 3. 初始化默认角色数据

```sql
-- 初始化默认角色
INSERT INTO sys_role (role_code, role_name, description) VALUES
('ADMIN', '系统管理员', '拥有所有权限'),
('MANAGER', '项目经理', '项目管理权限'),
('EMPLOYEE', '普通员工', '基础操作权限');
```

## 四、文件创建清单

### 4.1 配置类（config 包）

```
com.kele.gongshibackend.config/
├── JwtConfig.java                    # JWT 配置类
├── WebMvcConfig.java                 # 拦截器配置
└── BCryptConfig.java                 # BCrypt 加密配置
```

### 4.2 工具类（util 包）

```
com.kele.gongshibackend.util/
├── JwtUtil.java                      # JWT 生成和解析工具
├── BCryptUtil.java                  # 密码加密工具
└── SecurityUtil.java                 # 安全工具类（获取当前用户）
```

### 4.3 DTO 类（dto 包）

```
com.kele.gongshibackend.dto/
├── LoginRequest.java                 # 登录请求 DTO
├── LoginResponse.java                # 登录响应 DTO
├── PasswordChangeRequest.java       # 修改密码请求 DTO
└── UserInfoResponse.java            # 用户信息响应 DTO
```

### 4.4 异常处理（exception 包）

```
com.kele.gongshibackend.exception/
├── BusinessException.java           # 业务异常基类
├── AuthenticationException.java     # 认证异常
└── GlobalExceptionHandler.java      # 全局异常处理器
```

### 4.5 拦截器（interceptor 包）

```
com.kele.gongshibackend.interceptor/
├── AuthInterceptor.java             # 认证拦截器
└── PermissionInterceptor.java       # 权限拦截器
```

### 4.6 实体类（entity 包）

```
com.kele.gongshibackend/entity/
└── TokenBlacklist.java             # Token 黑名单实体
```

### 4.7 Mapper 层（mapper 包）

```
com.kele.gongshibackend/mapper/
└── TokenBlacklistMapper.java        # Token 黑名单 Mapper
```

### 4.8 Service 层（service 包）

```
com.kele.gongshibackend/service/
├── AuthService.java                 # 认证服务接口
├── TokenBlacklistService.java       # Token 黑名单服务接口
└── impl/
    ├── AuthServiceImpl.java         # 认证服务实现
    └── TokenBlacklistServiceImpl.java # Token 黑名单服务实现
```

### 4.9 Controller 层（controller 包）

```
com.kele.gongshibackend/controller/
└── AuthController.java             # 认证控制器
```

### 4.10 注解（annotation 包） 123

```
com.kele.gongshibackend/annotation/
├── RequireRole.java                 # 角色权限注解
└── IgnoreAuth.java                  # 忽略认证注解
```

## 五、核心文件代码结构设计

### 5.1 JwtUtil.java（JWT 工具类）

```java
package com.kele.gongshibackend.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtUtil {
    
    @Value("${jwt.secret:kele-gongshi-secret-key-2024}")
    private String secret;
    
    @Value("${jwt.expiration:86400000}")
    private Long expiration;
    
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }
    
    public String generateToken(Long userId, String username) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("username", username);
        return createToken(claims, username);
    }
    
    private String createToken(Map<String, Object> claims, String subject) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);
        
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(getSigningKey())
                .compact();
    }
    
    public Claims parseToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
    
    public Long getUserIdFromToken(String token) {
        Claims claims = parseToken(token);
        return claims.get("userId", Long.class);
    }
    
    public String getUsernameFromToken(String token) {
        Claims claims = parseToken(token);
        return claims.getSubject();
    }
    
    public Date getExpirationFromToken(String token) {
        Claims claims = parseToken(token);
        return claims.getExpiration();
    }
    
    public boolean isTokenExpired(String token) {
        try {
            Date expiration = getExpirationFromToken(token);
            return expiration.before(new Date());
        } catch (Exception e) {
            return true;
        }
    }
    
    public boolean validateToken(String token) {
        try {
            parseToken(token);
            return !isTokenExpired(token);
        } catch (Exception e) {
            return false;
        }
    }
}
```

### 5.2 BCryptUtil.java（密码加密工具）

```java
package com.kele.gongshibackend.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class BCryptUtil {
    
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
    
    public String encode(String rawPassword) {
        return encoder.encode(rawPassword);
    }
    
    public boolean matches(String rawPassword, String encodedPassword) {
        return encoder.matches(rawPassword, encodedPassword);
    }
}
```

### 5.3 SecurityUtil.java（安全工具类）

```java
package com.kele.gongshibackend.util;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

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
```

### 5.4 LoginRequest.java（登录请求 DTO）

```java
package com.kele.gongshibackend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;

@Data
@Schema(description = "登录请求")
public class LoginRequest {
    
    @Schema(description = "用户名")
    @NotBlank(message = "用户名不能为空")
    private String username;
    
    @Schema(description = "密码")
    @NotBlank(message = "密码不能为空")
    private String password;
}
```

### 5.5 LoginResponse.java（登录响应 DTO）

```java
package com.kele.gongshibackend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "登录响应")
public class LoginResponse {
    
    @Schema(description = "访问令牌")
    private String token;
    
    @Schema(description = "用户ID")
    private Long userId;
    
    @Schema(description = "用户名")
    private String username;
    
    @Schema(description = "真实姓名")
    private String realName;
    
    @Schema(description = "角色列表")
    private String[] roles;
}
```

### 5.6 PasswordChangeRequest.java（修改密码 DTO）

```java
package com.kele.gongshibackend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;

@Data
@Schema(description = "修改密码请求")
public class PasswordChangeRequest {
    
    @Schema(description = "原密码")
    @NotBlank(message = "原密码不能为空")
    private String oldPassword;
    
    @Schema(description = "新密码")
    @NotBlank(message = "新密码不能为空")
    private String newPassword;
}
```

### 5.7 UserInfoResponse.java（用户信息响应 DTO）

```java
package com.kele.gongshibackend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "用户信息响应")
public class UserInfoResponse {
    
    @Schema(description = "用户ID")
    private Long id;
    
    @Schema(description = "用户名")
    private String username;
    
    @Schema(description = "真实姓名")
    private String realName;
    
    @Schema(description = "邮箱")
    private String email;
    
    @Schema(description = "手机号")
    private String phone;
    
    @Schema(description = "状态")
    private Integer status;
    
    @Schema(description = "角色列表")
    private String[] roles;
    
    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}
```

### 5.8 BusinessException.java（业务异常）

```java
package com.kele.gongshibackend.exception;

import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {
    
    private final Integer code;
    
    public BusinessException(String message) {
        super(message);
        this.code = 500;
    }
    
    public BusinessException(Integer code, String message) {
        super(message);
        this.code = code;
    }
}
```

### 5.9 AuthenticationException.java（认证异常）

```java
package com.kele.gongshibackend.exception;

public class AuthenticationException extends BusinessException {
    
    public AuthenticationException(String message) {
        super(401, message);
    }
}
```

### 5.10 GlobalExceptionHandler.java（全局异常处理器）

```java
package com.kele.gongshibackend.exception;

import com.kele.gongshibackend.common.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    
    @ExceptionHandler(BusinessException.class)
    public Result<Void> handleBusinessException(BusinessException e) {
        log.error("业务异常: {}", e.getMessage());
        return Result.error(e.getCode(), e.getMessage());
    }
    
    @ExceptionHandler(AuthenticationException.class)
    public Result<Void> handleAuthenticationException(AuthenticationException e) {
        log.error("认证异常: {}", e.getMessage());
        return Result.error(401, e.getMessage());
    }
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<Void> handleValidationException(MethodArgumentNotValidException e) {
        FieldError fieldError = e.getBindingResult().getFieldError();
        String message = fieldError != null ? fieldError.getDefaultMessage() : "参数校验失败";
        log.error("参数校验异常: {}", message);
        return Result.error(400, message);
    }
    
    @ExceptionHandler(BindException.class)
    public Result<Void> handleBindException(BindException e) {
        FieldError fieldError = e.getFieldError();
        String message = fieldError != null ? fieldError.getDefaultMessage() : "参数绑定失败";
        log.error("参数绑定异常: {}", message);
        return Result.error(400, message);
    }
    
    @ExceptionHandler(Exception.class)
    public Result<Void> handleException(Exception e) {
        log.error("系统异常", e);
        return Result.error("系统异常，请联系管理员");
    }
}
```

### 5.11 RequireRole.java（角色权限注解）

```java
package com.kele.gongshibackend.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RequireRole {
    
    String[] value(); // 角色编码数组
}
```

### 5.12 IgnoreAuth.java（忽略认证注解）

```java
package com.kele.gongshibackend.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface IgnoreAuth {
}
```

### 5.13 TokenBlacklist.java（Token 黑名单实体）

```java
package com.kele.gongshibackend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("sys_token_blacklist")
public class TokenBlacklist implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String token;
    
    private Long userId;
    
    private LocalDateTime expireTime;
    
    private LocalDateTime createTime;
}
```

### 5.14 TokenBlacklistMapper.java

```java
package com.kele.gongshibackend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kele.gongshibackend.entity.TokenBlacklist;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface TokenBlacklistMapper extends BaseMapper<TokenBlacklist> {
}
```

### 5.15 TokenBlacklistService.java

```java
package com.kele.gongshibackend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.kele.gongshackend.entity.TokenBlacklist;

public interface TokenBlacklistService extends IService<TokenBlacklist> {
    
    boolean addToBlacklist(String token, Long userId);
    
    boolean isInBlacklist(String token);
    
    void cleanExpiredTokens();
}
```

### 5.16 TokenBlacklistServiceImpl.java

```java
package com.kele.gongshibackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kele.gongshibackend.entity.TokenBlacklist;
import com.kele.gongshibackend.mapper.TokenBlacklistMapper;
import com.kele.gongshibackend.service.TokenBlacklistService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
public class TokenBlacklistServiceImpl 
        extends ServiceImpl<TokenBlacklistMapper, TokenBlacklist> 
        implements TokenBlacklistService {
    
    @Override
    public boolean addToBlacklist(String token, Long userId) {
        TokenBlacklist blacklist = new TokenBlacklist();
        blacklist.setToken(token);
        blacklist.setUserId(userId);
        blacklist.setExpireTime(LocalDateTime.now().plusDays(1)); // 默认1天过期
        return save(blacklist);
    }
    
    @Override
    public boolean isInBlacklist(String token) {
        QueryWrapper<TokenBlacklist> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("token", token);
        queryWrapper.gt("expire_time", LocalDateTime.now());
        return count(queryWrapper) > 0;
    }
    
    @Scheduled(cron = "0 0 2 * * ?") // 每天凌晨2点清理过期Token
    @Override
    public void cleanExpiredTokens() {
        QueryWrapper<TokenBlacklist> queryWrapper = new QueryWrapper<>();
        queryWrapper.lt("expire_time", LocalDateTime.now());
        remove(queryWrapper);
        log.info("已清理过期的Token黑名单记录");
    }
}
```

### 5.17 AuthService.java

```java
package com.kele.gongshibackend.service;

import com.kele.gongshibackend.dto.LoginRequest;
import com.kele.gongshibackend.dto.LoginResponse;
import com.kele.gongshibackend.dto.PasswordChangeRequest;
import com.kele.gongshibackend.dto.UserInfoResponse;

public interface AuthService {
    
    LoginResponse login(LoginRequest request);
    
    void logout(String token);
    
    UserInfoResponse getUserInfo(Long userId);
    
    String[] getUserRoles(Long userId);
    
    boolean hasRole(Long userId, String roleCode);
    
    void changePassword(Long userId, PasswordChangeRequest request);
    
    String encryptPassword(String rawPassword);
}
```

### 5.18 AuthServiceImpl.java

```java
package com.kele.gongshibackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.kele.gongshibackend.dto.*;
import com.kele.gongshibackend.entity.SysRole;
import com.kele.gongshibackend.entity.SysUserRole;
import com.kele.gongshibackend.entity.User;
import com.kele.gongshibackend.exception.AuthenticationException;
import com.kele.gongshibackend.exception.BusinessException;
import com.kele.gongshibackend.service.AuthService;
import com.kele.gongshibackend.service.SysRoleService;
import com.kele.gongshibackend.service.SysUserRoleService;
import com.kele.gongshibackend.service.TokenBlacklistService;
import com.kele.gongshibackend.service.UserService;
import com.kele.gongshibackend.util.BCryptUtil;
import com.kele.gongshibackend.util.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class AuthServiceImpl implements AuthService {
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private SysUserRoleService sysUserRoleService;
    
    @Autowired
    private SysRoleService sysRoleService;
    
    @Autowired
    private TokenBlacklistService tokenBlacklistService;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @Autowired
    private BCryptUtil bCryptUtil;
    
    @Override
    public LoginResponse login(LoginRequest request) {
        // 查询用户
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", request.getUsername());
        User user = userService.getOne(queryWrapper);
        
        if (user == null) {
            throw new AuthenticationException("用户名或密码错误");
        }
        
        // 检查用户状态
        if (user.getStatus() == 0) {
            throw new AuthenticationException("账号已被禁用");
        }
        
        // 验证密码
        if (!bCryptUtil.matches(request.getPassword(), user.getPassword())) {
            throw new AuthenticationException("用户名或密码错误");
        }
        
        // 生成Token
        String token = jwtUtil.generateToken(user.getId(), user.getUsername());
        
        // 获取用户角色
        String[] roles = getUserRoles(user.getId());
        
        return new LoginResponse(token, user.getId(), user.getUsername(), 
                                 user.getRealName(), roles);
    }
    
    @Override
    @Transactional
    public void logout(String token) {
        Long userId = jwtUtil.getUserIdFromToken(token);
        tokenBlacklistService.addToBlacklist(token, userId);
    }
    
    @Override
    public UserInfoResponse getUserInfo(Long userId) {
        User user = userService.getById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        
        String[] roles = getUserRoles(userId);
        
        return new UserInfoResponse(
            user.getId(), user.getUsername(), user.getRealName(),
            user.getEmail(), user.getPhone(), user.getStatus(),
            roles, user.getCreateTime()
        );
    }
    
    @Override
    public String[] getUserRoles(Long userId) {
        QueryWrapper<SysUserRole> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        List<SysUserRole> userRoles = sysUserRoleService.list(queryWrapper);
        
        if (userRoles.isEmpty()) {
            return new String[0];
        }
        
        List<Long> roleIds = userRoles.stream()
                .map(SysUserRole::getRoleId)
                .collect(Collectors.toList());
        
        List<SysRole> roles = sysRoleService.listByIds(roleIds);
        
        return roles.stream()
                .map(SysRole::getRoleCode)
                .toArray(String[]::new);
    }
    
    @Override
    public boolean hasRole(Long userId, String roleCode) {
        String[] roles = getUserRoles(userId);
        for (String role : roles) {
            if (role.equals(roleCode)) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    @Transactional
    public void changePassword(Long userId, PasswordChangeRequest request) {
        User user = userService.getById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        
        // 验证原密码
        if (!bCryptUtil.matches(request.getOldPassword(), user.getPassword())) {
            throw new BusinessException("原密码错误");
        }
        
        // 加密新密码
        String encryptedPassword = bCryptUtil.encode(request.getNewPassword());
        user.setPassword(encryptedPassword);
        
        if (!userService.updateById(user)) {
            throw new BusinessException("密码修改失败");
        }
    }
    
    @Override
    public String encryptPassword(String rawPassword) {
        return bCryptUtil.encode(rawPassword);
    }
}
```

### 5.19 AuthInterceptor.java（认证拦截器）

```java
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
```

### 5.20 PermissionInterceptor.java（权限拦截器）

```java
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
```

### 5.21 WebMvcConfig.java（拦截器配置）

```java
package com.kele.gongshibackend.config;

import com.kele.gongshibackend.interceptor.AuthInterceptor;
import com.kele.gongshibackend.interceptor.PermissionInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableScheduling
public class WebMvcConfig implements WebMvcConfigurer {
    
    @Autowired
    private AuthInterceptor authInterceptor;
    
    @Autowired
    private PermissionInterceptor permissionInterceptor;
    
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 认证拦截器
        registry.addInterceptor(authInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns("/auth/login", "/swagger-ui/**", "/v3/api-docs/**");
        
        // 权限拦截器
        registry.addInterceptor(permissionInterceptor)
                .addPathPatterns("/**");
    }
}
```

### 5.22 JwtConfig.java（JWT 配置）

```java
package com.kele.gongshibackend.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "jwt")
public class JwtConfig {
    
    private String secret = "kele-gongshi-secret-key-2024";
    private Long expiration = 86400000L; // 24小时
    
    public String getSecret() {
        return secret;
    }
    
    public void setSecret(String secret) {
        this.secret = secret;
    }
    
    public Long getExpiration() {
        return expiration;
    }
    
    public void setExpiration(Long expiration) {
        this.expiration = expiration;
    }
}
```

### 5.23 BCryptConfig.java（BCrypt 配置）

```java
package com.kele.gongshibackend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class BCryptConfig {
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
```

### 5.24 AuthController.java（认证控制器）

```java
package com.kele.gongshibackend.controller;

import com.kele.gongshibackend.annotation.IgnoreAuth;
import com.kele.gongshibackend.common.Result;
import com.kele.gongshibackend.dto.LoginRequest;
import com.kele.gongshibackend.dto.LoginResponse;
import com.kele.gongshibackend.dto.PasswordChangeRequest;
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
```

## 六、配置文件更新

在 `application.yaml` 中添加 JWT 配置：

```yaml
jwt:
  secret: kele-gongshi-secret-key-change-this-in-production-2024
  expiration: 86400000  # Token过期时间（毫秒），默认24小时
```

## 七、密码迁移方案

### 7.1 密码迁移脚本

```sql
-- 密码迁移SQL（将现有明文密码迁移到BCrypt）
-- 注意：需要在应用程序中执行，因为BCrypt是应用层加密

-- 创建临时表存储新密码
CREATE TABLE sys_user_temp AS SELECT id, username, password FROM sys_user;
```

### 7.2 密码迁移工具类（创建临时迁移脚本）

创建 `PasswordMigrationTool.java`（一次性使用）：

```java
package com.kele.gongshibackend.util;

import com.kele.gongshibackend.entity.User;
import com.kele.gongshibackend.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PasswordMigrationTool implements CommandLineRunner {
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private BCryptUtil bCryptUtil;
    
    @Override
    public void run(String... args) {
        // 只在生产环境或指定配置下执行
        boolean enableMigration = false; // 设置为true执行迁移
        if (!enableMigration) {
            return;
        }
        
        log.info("开始执行密码迁移...");
        
        List<User> users = userService.list();
        for (User user : users) {
            // 检查是否已经是BCrypt格式（BCrypt哈希以$2a$或$2b$开头）
            if (!user.getPassword().startsWith("$2")) {
                String oldPassword = user.getPassword();
                String newPassword = bCryptUtil.encode(oldPassword);
                user.setPassword(newPassword);
                userService.updateById(user);
                log.info("用户 {} 密码已迁移", user.getUsername());
            }
        }
        
        log.info("密码迁移完成！");
    }
}
```

### 7.3 迁移步骤

1. **备份现有数据**：
   ```sql
   -- 备份用户表
   CREATE TABLE sys_user_backup AS SELECT * FROM sys_user;
   ```

2. **启用迁移工具**：
    - 在 `PasswordMigrationTool.java` 中设置 `enableMigration = true`
    - 启动应用程序一次
    - 应用程序会自动将所有明文密码转换为 BCrypt 哈希

3. **禁用迁移工具**：
    - 设置 `enableMigration = false`
    - 重启应用程序

4. **验证迁移**：
    - 使用原有密码登录测试
    - 确认所有用户都能正常登录

## 八、接口实现顺序

### 第一阶段：基础设施（优先级：高）

1. **添加依赖** - 在 pom.xml 中添加 JWT、参数校验、Spring Security Crypto
2. **创建配置类** - JwtConfig、BCryptConfig、WebMvcConfig
3. **创建工具类** - JwtUtil、BCryptUtil、SecurityUtil
4. **创建异常处理** - BusinessException、AuthenticationException、GlobalExceptionHandler
5. **数据库变更** - 创建 sys_token_blacklist 表

### 第二阶段：核心功能（优先级：高）

6. **创建 DTO 类** - LoginRequest、LoginResponse、PasswordChangeRequest、UserInfoResponse
7. **创建实体和 Mapper** - TokenBlacklist、TokenBlacklistMapper
8. **创建注解** - RequireRole、IgnoreAuth
9. **创建 Token 黑名单服务** - TokenBlacklistService、TokenBlacklistServiceImpl
10. **创建认证服务** - AuthService、AuthServiceImpl

### 第三阶段：拦截器和控制器（优先级：中）

11. **创建拦截器** - AuthInterceptor、PermissionInterceptor
12. **创建认证控制器** - AuthController
13. **配置拦截器** - 在 WebMvcConfig 中注册拦截器

### 第四阶段：密码迁移（优先级：中）

14. **创建迁移工具** - PasswordMigrationTool
15. **执行密码迁移** - 按照迁移方案执行

### 第五阶段：测试验证（优先级：高）

16. **单元测试** - 为各服务层编写单元测试
17. **集成测试** - 测试完整的认证流程
18. **接口测试** - 使用 Swagger 测试所有接口

## 九、测试验证方案

### 9.1 接口测试用例

#### 测试用例 1：用户登录

```bash
POST /auth/login
Content-Type: application/json

{
  "username": "admin",
  "password": "admin123"
}

预期结果：
{
  "code": 200,
  "message": "登录成功",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiJ9...",
    "userId": 1,
    "username": "admin",
    "realName": "管理员",
    "roles": ["ADMIN"]
  }
}
```

#### 测试用例 2：错误密码登录

```bash
POST /auth/login
Content-Type: application/json

{
  "username": "admin",
  "password": "wrong_password"
}

预期结果：
{
  "code": 401,
  "message": "用户名或密码错误"
}
```

#### 测试用例 3：获取用户信息（需要认证）

```bash
GET /auth/userinfo
Authorization: Bearer {token}

预期结果：
{
  "code": 200,
  "message": "查询成功",
  "data": {
    "id": 1,
    "username": "admin",
    "realName": "管理员",
    "email": "admin@example.com",
    "phone": "13800138000",
    "status": 1,
    "roles": ["ADMIN"]
  }
}
```

#### 测试用例 4：未认证访问

```bash
GET /auth/userinfo

预期结果：
{
  "code": 401,
  "message": "未登录或Token已过期"
}
```

#### 测试用例 5：用户退出

```bash
POST /auth/logout
Authorization: Bearer {token}

预期结果：
{
  "code": 200,
  "message": "退出成功"
}
```

#### 测试用例 6：使用已退出的Token访问

```bash
GET /auth/userinfo
Authorization: Bearer {expired_token}

预期结果：
{
  "code": 401,
  "message": "Token已失效，请重新登录"
}
```

#### 测试用例 7：修改密码

```bash
PUT /auth/password
Authorization: Bearer {token}
Content-Type: application/json

{
  "oldPassword": "admin123",
  "newPassword": "newpassword123"
}

预期结果：
{
  "code": 200,
  "message": "密码修改成功"
}
```

#### 测试用例 8：获取角色列表

```bash
GET /auth/roles
Authorization: Bearer {token}

预期结果：
{
  "code": 200,
  "message": "查询成功",
  "data": ["ADMIN"]
}
```

#### 测试用例 9：检查角色权限

```bash
GET /auth/hasRole/ADMIN
Authorization: Bearer {token}

预期结果：
{
  "code": 200,
  "message": "查询成功",
  "data": true
}
```

### 9.2 单元测试示例

创建 `AuthServiceTest.java`：

```java
package com.kele.gongshibackend.service;

import com.kele.gongshibackend.dto.LoginRequest;
import com.kele.gongshibackend.dto.LoginResponse;
import com.kele.gongshibackend.dto.PasswordChangeRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class AuthServiceTest {
    
    @Autowired
    private AuthService authService;
    
    @Test
    public void testLoginSuccess() {
        LoginRequest request = new LoginRequest();
        request.setUsername("admin");
        request.setPassword("admin123");
        
        LoginResponse response = authService.login(request);
        
        assertNotNull(response);
        assertNotNull(response.getToken());
        assertEquals("admin", response.getUsername());
    }
    
    @Test
    public void testLoginWithWrongPassword() {
        LoginRequest request = new LoginRequest();
        request.setUsername("admin");
        request.setPassword("wrongpassword");
        
        assertThrows(AuthenticationException.class, () -> {
            authService.login(request);
        });
    }
    
    @Test
    public void testChangePassword() {
        PasswordChangeRequest request = new PasswordChangeRequest();
        request.setOldPassword("admin123");
        request.setNewPassword("newpassword123");
        
        assertDoesNotThrow(() -> {
            authService.changePassword(1L, request);
        });
    }
}
```

### 9.3 集成测试流程

1. **准备测试数据**：
   ```sql
   INSERT INTO sys_user (username, password, real_name, status) VALUES
   ('testuser', '$2a$10$test', '测试用户', 1);
   
   INSERT INTO sys_role (role_code, role_name) VALUES
   ('TEST', '测试角色');
   
   INSERT INTO sys_user_role (user_id, role_id) VALUES
   ((SELECT id FROM sys_user WHERE username = 'testuser'), 
    (SELECT id FROM sys_role WHERE role_code = 'TEST'));
   ```

2. **执行完整流程测试**：
    - 登录 → 获取Token
    - 使用Token访问需要认证的接口
    - 修改密码
    - 退出登录
    - 验证Token失效

3. **权限测试**：
    - 创建不同角色的用户
    - 测试 `@RequireRole` 注解的功能
    - 验证权限拦截器

## 十、注意事项

### 10.1 安全建议

1. **JWT Secret**：生产环境必须使用强随机密钥，不要使用默认值
2. **Token 过期时间**：建议设置为较短时间（如2小时），并支持刷新Token机制
3. **HTTPS**：生产环境必须使用 HTTPS 传输 Token
4. **密码策略**：建议实现密码强度校验
5. **登录限流**：建议添加登录失败次数限制，防止暴力破解

### 10.2 扩展建议

1. **Token 刷新机制**：实现 refreshToken 机制，避免频繁登录
2. **记住我功能**：支持"记住我"功能，延长 Token 有效期
3. **多端登录控制**：限制同一账号的并发登录数
4. **登录日志**：记录用户登录行为
5. **验证码**：登录时添加图形验证码

### 10.3 兼容性说明

1. **Jakarta EE**：Spring Boot 3.x 使用 jakarta.* 命名空间，不是 javax.*
2. **MyBatis-Plus**：使用 `@TableName` 和 `@TableId` 注解
3. **Lombok**：使用 `@Data`、`@Slf4j` 等注解
4. **Swagger**：使用 OpenAPI 3.0 (knife4j)

### Critical Files for Implementation

Based on the analysis, here are the most critical files for implementing this authentication system:

- **E:\my-code\kele-weixinshare-code\ai-gongshi\gongshi-backend\pom.xml** - Maven dependencies for JWT, validation, and Spring Security
- **E:\my-code\kele-weixinshare-code\ai-gongshi\gongshi-backend\src\main\java\com\kele\gongshibackend\service\impl\AuthServiceImpl.java** - Core authentication logic implementation
- **E:\my-code\kele-weixinshare-code\ai-gongshi\gongshi-backend\src\main\java\com\kele\gongshibackend\util\JwtUtil.java** - JWT token generation and validation
- **E:\my-code\kele-weixinshare-code\ai-gongshi\gongshi-backend\src\main\java\com\kele\gongshibackend\interceptor\AuthInterceptor.java** - Authentication interceptor for request filtering
- **E:\my-code\kele-weixinshare-code\ai-gongshi\gongshi-backend\src\main\java\com\kele\gongshibackend\controller\AuthController.java** - REST API endpoints for authentication
  agentId: a529a5d (for resuming to continue this agent's work if needed)
  <usage>total_tokens: 33090
  tool_uses: 25
  duration_ms: 236752</usage>