package com.kele.gongshibackend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kele.gongshibackend.entity.SysRole;
import org.apache.ibatis.annotations.Mapper;

/**
 * 角色表 Mapper 接口
 */
@Mapper
public interface SysRoleMapper extends BaseMapper<SysRole> {
}
