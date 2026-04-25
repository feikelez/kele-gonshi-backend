package com.kele.gongshibackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kele.gongshibackend.entity.ProjectMember;
import com.kele.gongshibackend.mapper.ProjectMemberMapper;
import com.kele.gongshibackend.service.ProjectMemberService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * 项目成员服务实现类
 *
 * @author kele
 * @since 2026-04-05
 */
@Service
public class ProjectMemberServiceImpl extends ServiceImpl<ProjectMemberMapper, ProjectMember> implements ProjectMemberService {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void assignMembers(Long projectId, List<Long> userIds) {
        // 1. 先删除该项目所有现有的成员关联
        QueryWrapper<ProjectMember> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("project_id", projectId);
        this.remove(queryWrapper);

        // 2. 再新增新分配的成员
        if (userIds != null && !userIds.isEmpty()) {
            List<ProjectMember> members = new ArrayList<>();
            LocalDate joinDate = LocalDate.now();
            for (Long userId : userIds) {
                ProjectMember member = new ProjectMember();
                member.setProjectId(projectId);
                member.setUserId(userId);
                member.setJoinDate(joinDate);
                members.add(member);
            }
            this.saveBatch(members);
        }
    }
}
