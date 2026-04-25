package com.kele.gongshibackend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kele.gongshibackend.entity.Project;
import com.kele.gongshibackend.mapper.ProjectMapper;
import com.kele.gongshibackend.service.ProjectService;
import org.springframework.stereotype.Service;

/**
 * 项目服务实现类
 *
 * @author kele
 * @since 2026-04-05
 */
@Service
public class ProjectServiceImpl extends ServiceImpl<ProjectMapper, Project> implements ProjectService {
}
