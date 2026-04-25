package com.kele.gongshibackend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.kele.gongshibackend.entity.ProjectMember;

import java.util.List;

/**
 * 项目成员服务接口
 *
 * @author kele
 * @since 2026-04-05
 */
public interface ProjectMemberService extends IService<ProjectMember> {

    /**
     * 分配项目成员（原子操作：先删除后新增）
     *
     * @param projectId 项目ID
     * @param userIds 用户ID列表
     */
    void assignMembers(Long projectId, List<Long> userIds);
}
