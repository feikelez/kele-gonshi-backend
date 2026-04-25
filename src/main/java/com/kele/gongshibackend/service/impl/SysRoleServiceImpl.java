package com.kele.gongshibackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kele.gongshibackend.entity.SysRole;
import com.kele.gongshibackend.mapper.SysRoleMapper;
import com.kele.gongshibackend.service.SysRoleService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * 角色表 Service 实现类
 */
@Service
public class SysRoleServiceImpl extends ServiceImpl<SysRoleMapper, SysRole> implements SysRoleService {

    @Override
    public Page<SysRole> getRolePage(Long current, Long size, String roleName) {
        Page<SysRole> page = new Page<>(current, size);
        LambdaQueryWrapper<SysRole> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(roleName)) {
            wrapper.like(SysRole::getRoleName, roleName);
        }
        wrapper.orderByDesc(SysRole::getCreateTime);
        return this.page(page, wrapper);
    }
}
