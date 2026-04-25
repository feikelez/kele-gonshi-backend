package com.kele.gongshibackend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.kele.gongshibackend.entity.WorkRecord;
import com.kele.gongshibackend.vo.ProjectWeeklyStatsVO;
import com.kele.gongshibackend.vo.TopProjectsStatsVO;

/**
 * 工时记录服务接口
 *
 * @author kele
 * @since 2026-04-05
 */
public interface WorkRecordService extends IService<WorkRecord> {

    /**
     * 按项目按周统计工时
     * @param projectId 项目ID
     * @param month 年月，格式 YYYY-MM
     * @return 统计结果
     */
    ProjectWeeklyStatsVO getProjectWeeklyStats(Long projectId, String month);

    /**
     * 统计工时排名前N的项目
     * @param month 年月，格式 YYYY-MM
     * @param limit 返回数量
     * @return 统计结果
     */
    TopProjectsStatsVO getTopProjectsStats(String month, Integer limit);
}
