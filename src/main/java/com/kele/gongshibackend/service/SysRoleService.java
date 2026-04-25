package com.kele.gongshibackend.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.kele.gongshibackend.entity.SysRole;

/**
 * 角色表 Service 接口
 */
public interface SysRoleService extends IService<SysRole> {

    /**
     * 分页查询角色列表
     *
     * @param current 当前页
     * @param size    每页条数
     * @param roleName 角色名称（模糊查询）
     * @return 分页结果
     */
    Page<SysRole> getRolePage(Long current, Long size, String roleName);
}
