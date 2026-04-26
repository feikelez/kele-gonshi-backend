package com.kele.gongshibackend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kele.gongshibackend.entity.Project;
import com.kele.gongshibackend.entity.WorkRecord;
import com.kele.gongshibackend.mapper.WorkRecordMapper;
import com.kele.gongshibackend.service.ProjectService;
import com.kele.gongshibackend.service.WorkRecordService;
import com.kele.gongshibackend.vo.DailyWorkHoursVO;
import com.kele.gongshibackend.vo.ProjectWeeklyStatsVO;
import com.kele.gongshibackend.vo.TopProjectsStatsVO;
import com.kele.gongshibackend.vo.WorkRecordExportQuery;
import com.kele.gongshibackend.vo.WorkRecordExportVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 工时记录服务实现类
 *
 * @author kele
 * @since 2026-04-05
 */
@Service
public class WorkRecordServiceImpl extends ServiceImpl<WorkRecordMapper, WorkRecord> implements WorkRecordService {

    @Autowired
    private ProjectService projectService;

    private static final String[] DAY_LABELS = {"周一", "周二", "周三", "周四", "周五", "周六", "周日"};

    @Override
    public ProjectWeeklyStatsVO getProjectWeeklyStats(Long projectId, String month) {
        ProjectWeeklyStatsVO vo = new ProjectWeeklyStatsVO();
        vo.setProjectId(projectId);
        vo.setMonth(month);

        Project project = projectService.getById(projectId);
        if (project != null) {
            vo.setProjectName(project.getProjectName());
        } else {
            vo.setProjectName("");
        }

        List<Map<String, Object>> weeklyData = baseMapper.selectWeeklyHoursByProjectAndMonth(projectId, month);

        Map<Integer, BigDecimal> hoursMap = new HashMap<>();
        for (Map<String, Object> row : weeklyData) {
            Integer week = ((Number) row.get("week")).intValue();
            BigDecimal hours = (BigDecimal) row.get("hours");
            hoursMap.put(week, hours);
        }

        List<ProjectWeeklyStatsVO.WeekHours> weeks = new ArrayList<>();
        for (int i = 1; i <= 4; i++) {
            ProjectWeeklyStatsVO.WeekHours weekHours = new ProjectWeeklyStatsVO.WeekHours();
            weekHours.setWeek(i);
            weekHours.setHours(hoursMap.getOrDefault(i, BigDecimal.ZERO));
            weeks.add(weekHours);
        }
        vo.setWeeks(weeks);

        return vo;
    }

    @Override
    public TopProjectsStatsVO getTopProjectsStats(String month, Integer limit) {
        TopProjectsStatsVO vo = new TopProjectsStatsVO();
        vo.setMonth(month);

        String lastMonth = calcLastMonth(month);

        List<Map<String, Object>> data = baseMapper.selectTopProjectHoursByMonth(month, lastMonth, limit);

        List<TopProjectsStatsVO.ProjectHours> projects = new ArrayList<>();
        for (Map<String, Object> row : data) {
            TopProjectsStatsVO.ProjectHours ph = new TopProjectsStatsVO.ProjectHours();
            ph.setProjectId(((Number) row.get("projectId")).longValue());
            ph.setProjectName((String) row.get("projectName"));
            ph.setTotalHours((BigDecimal) row.get("totalHours"));
            ph.setTrend(((Number) row.get("trend")).intValue());
            projects.add(ph);
        }
        vo.setProjects(projects);

        return vo;
    }

    private String calcLastMonth(String month) {
        LocalDate date = LocalDate.parse(month + "-01", DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        LocalDate lastMonth = date.minusMonths(1);
        return lastMonth.format(DateTimeFormatter.ofPattern("yyyy-MM"));
    }

    @Override
    public List<WorkRecordExportVO> getExportData(WorkRecordExportQuery query) {
        return baseMapper.selectForExport(query);
    }

    @Override
    public List<DailyWorkHoursVO> getDailyWorkHours(Long userId, String startDate, String endDate) {
        List<Map<String, Object>> rawData = baseMapper.selectDailyWorkHours(userId, startDate, endDate);

        Map<String, BigDecimal> hoursMap = new HashMap<>();
        for (Map<String, Object> row : rawData) {
            String day = (String) row.get("day");
            BigDecimal hours = (BigDecimal) row.get("totalHours");
            hoursMap.put(day, hours);
        }

        LocalDate start = LocalDate.parse(startDate, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        LocalDate end = LocalDate.parse(endDate, DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        List<DailyWorkHoursVO> result = new ArrayList<>();
        LocalDate current = start;
        while (!current.isAfter(end)) {
            String dayStr = current.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            BigDecimal hours = hoursMap.getOrDefault(dayStr, BigDecimal.ZERO);

            WeekFields weekFields = WeekFields.of(java.util.Locale.CHINA);
            int dayOfWeek = current.get(weekFields.dayOfWeek());
            int labelIndex = dayOfWeek - 1;

            result.add(new DailyWorkHoursVO(DAY_LABELS[labelIndex], hours));

            current = current.plusDays(1);
        }

        return result;
    }
}
