package com.kele.gongshibackend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kele.gongshibackend.entity.WorkRecord;
import org.apache.ibatis.annotations.Mapper;

/**
 * 工时记录Mapper接口
 *
 * @author kele
 * @since 2026-04-05
 */
@Mapper
public interface WorkRecordMapper extends BaseMapper<WorkRecord> {
}
