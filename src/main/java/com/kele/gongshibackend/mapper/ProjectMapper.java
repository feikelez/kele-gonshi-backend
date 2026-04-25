package com.kele.gongshibackend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kele.gongshibackend.entity.Project;
import org.apache.ibatis.annotations.Mapper;

/**
 * 项目Mapper接口
 *
 * @author kele
 * @since 2026-04-05
 */
@Mapper
public interface ProjectMapper extends BaseMapper<Project> {
}
