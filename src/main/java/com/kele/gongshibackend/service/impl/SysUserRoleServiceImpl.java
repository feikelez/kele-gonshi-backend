package com.kele.gongshibackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kele.gongshibackend.entity.SysUserRole;
import com.kele.gongshibackend.mapper.SysUserRoleMapper;
import com.kele.gongshibackend.service.SysUserRoleService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户角色关联表 Service 实现类
 */
@Service
public class SysUserRoleServiceImpl extends ServiceImpl<SysUserRoleMapper, SysUserRole> implements SysUserRoleService {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean assignRoles(Long userId, List<Long> roleIds) {
        if (userId == null || CollectionUtils.isEmpty(roleIds)) {
            return false;
        }

        // 先删除该用户的所有角色
        this.removeUserRoles(userId);

        // 批量插入新的用户角色关系
        List<SysUserRole> userRoleList = roleIds.stream()
                .map(roleId -> {
                    SysUserRole userRole = new SysUserRole();
                    userRole.setUserId(userId);
                    userRole.setRoleId(roleId);
                    return userRole;
                })
                .collect(Collectors.toList());

        return this.saveBatch(userRoleList);
    }

    @Override
    public List<Long> getRoleIdsByUserId(Long userId) {
        if (userId == null) {
            return new ArrayList<>();
        }

        LambdaQueryWrapper<SysUserRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUserRole::getUserId, userId);

        List<SysUserRole> list = this.list(wrapper);
        return list.stream()
                .map(SysUserRole::getRoleId)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean removeUserRoles(Long userId) {
        if (userId == null) {
            return false;
        }

        LambdaQueryWrapper<SysUserRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUserRole::getUserId, userId);
        return this.remove(wrapper);
    }
}
