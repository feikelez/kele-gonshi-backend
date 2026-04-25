package com.kele.gongshibackend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kele.gongshibackend.entity.WorkRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 工时记录Mapper接口
 *
 * @author kele
 * @since 2026-04-05
 */
@Mapper
public interface WorkRecordMapper extends BaseMapper<WorkRecord> {

    /**
     * 按项目按月统计每周工时
     * @param projectId 项目ID
     * @param month 年月，格式 YYYY-MM
     * @return 每周工时列表，每条记录包含 week 和 hours
     */
    List<Map<String, Object>> selectWeeklyHoursByProjectAndMonth(
            @Param("projectId") Long projectId,
            @Param("month") String month);

    /**
     * 统计工时排名前N的项目
     * @param month 年月，格式 YYYY-MM
     * @param lastMonth 上月年月，格式 YYYY-MM
     * @param limit 返回数量
     * @return 项目工时列表
     */
    List<Map<String, Object>> selectTopProjectHoursByMonth(
            @Param("month") String month,
            @Param("lastMonth") String lastMonth,
            @Param("limit") Integer limit);
}
