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
    @Transactional
    public void register(RegisterRequest request) {
        // 验证两次密码是否一致
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new BusinessException("两次密码输入不一致");
        }

        // 检查用户名是否已存在
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", request.getUsername());
        if (userService.count(queryWrapper) > 0) {
            throw new BusinessException("用户名已存在");
        }

        // 检查邮箱是否已存在
        if (request.getEmail() != null && !request.getEmail().isEmpty()) {
            queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("email", request.getEmail());
            if (userService.count(queryWrapper) > 0) {
                throw new BusinessException("邮箱已被注册");
            }
        }

        // 检查手机号是否已存在
        if (request.getPhone() != null && !request.getPhone().isEmpty()) {
            queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("phone", request.getPhone());
            if (userService.count(queryWrapper) > 0) {
                throw new BusinessException("手机号已被注册");
            }
        }

        // 创建用户
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(bCryptUtil.encode(request.getPassword()));
        user.setRealName(request.getRealName());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setStatus(1); // 默认启用

        if (!userService.save(user)) {
            throw new BusinessException("注册失败");
        }

        // 为新用户分配默认角色（EMPLOYEE）
        SysRole defaultRole = sysRoleService.getOne(new QueryWrapper<SysRole>().eq("role_code", "EMPLOYEE"));

        if (defaultRole != null) {
            SysUserRole userRole = new SysUserRole();
            userRole.setUserId(user.getId());
            userRole.setRoleId(defaultRole.getId());
            sysUserRoleService.save(userRole);
            log.info("用户 {} 已分配默认角色: {}", user.getUsername(), defaultRole.getRoleCode());
        }

        log.info("用户注册成功: {}", user.getUsername());
    }

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
