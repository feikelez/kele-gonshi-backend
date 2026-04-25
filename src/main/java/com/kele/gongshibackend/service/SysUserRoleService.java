package com.kele.gongshibackend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.kele.gongshibackend.entity.SysUserRole;

import java.util.List;

/**
 * 用户角色关联表 Service 接口
 */
public interface SysUserRoleService extends IService<SysUserRole> {

    /**
     * 为用户分配角色
     *
     * @param userId  用户ID
     * @param roleIds 角色ID列表
     * @return 是否成功
     */
    boolean assignRoles(Long userId, List<Long> roleIds);

    /**
     * 获取用户的角色ID列表
     *
     * @param userId 用户ID
     * @return 角色ID列表
     */
    List<Long> getRoleIdsByUserId(Long userId);

    /**
     * 删除用户的所有角色
     *
     * @param userId 用户ID
     * @return 是否成功
     */
    boolean removeUserRoles(Long userId);
}
