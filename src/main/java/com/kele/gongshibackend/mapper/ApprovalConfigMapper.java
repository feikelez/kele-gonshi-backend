package com.kele.gongshibackend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kele.gongshibackend.entity.ApprovalConfig;
import org.apache.ibatis.annotations.Mapper;

/**
 * 审批人配置Mapper接口
 *
 * @author kele
 * @since 2026-04-05
 */
@Mapper
public interface ApprovalConfigMapper extends BaseMapper<ApprovalConfig> {
}
