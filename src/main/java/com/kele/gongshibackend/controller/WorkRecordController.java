package com.kele.gongshibackend.controller;

import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kele.gongshibackend.common.Result;
import com.kele.gongshibackend.entity.WorkRecord;
import com.kele.gongshibackend.service.WorkRecordService;
import com.kele.gongshibackend.vo.ProjectWeeklyStatsVO;
import com.kele.gongshibackend.vo.TopProjectsStatsVO;
import com.kele.gongshibackend.vo.WorkRecordExportQuery;
import com.kele.gongshibackend.vo.WorkRecordExportVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

import com.kele.gongshibackend.util.SecurityUtil;

/**
 * 工时记录控制器
 *
 * @author kele
 * @since 2026-04-05
 */
@Tag(name = "工时记录管理", description = "工时记录增删改查接口")
@RestController
@RequestMapping("/work-record")
public class WorkRecordController {

    @Autowired
    private WorkRecordService workRecordService;

    /**
     * 新增工时记录
     */
    @Operation(summary = "新增工时记录")
    @PostMapping
    public Result<WorkRecord> add(@RequestBody WorkRecord workRecord) {
        boolean success = workRecordService.save(workRecord);
        if (success) {
            return Result.success("新增成功", workRecord);
        }
        return Result.error("新增失败");
    }

    /**
     * 根据ID删除工时记录
     */
    @Operation(summary = "根据ID删除工时记录")
    @DeleteMapping("/{id}")
    public Result<Void> delete(
            @Parameter(description = "工时记录ID") @PathVariable Long id) {
        boolean success = workRecordService.removeById(id);
        if (success) {
            return Result.success("删除成功");
        }
        return Result.error("删除失败");
    }

    /**
     * 批量删除工时记录
     */
    @Operation(summary = "批量删除工时记录")
    @DeleteMapping("/batch")
    public Result<Void> deleteBatch(@RequestBody List<Long> ids) {
        boolean success = workRecordService.removeByIds(ids);
        if (success) {
            return Result.success("批量删除成功");
        }
        return Result.error("批量删除失败");
    }

    /**
     * 修改工时记录
     */
    @Operation(summary = "修改工时记录")
    @PutMapping
    public Result<WorkRecord> update(@RequestBody WorkRecord workRecord) {
        boolean success = workRecordService.updateById(workRecord);
        if (success) {
            return Result.success("修改成功", workRecord);
        }
        return Result.error("修改失败");
    }

    /**
     * 根据ID查询工时记录
     */
    @Operation(summary = "根据ID查询工时记录")
    @GetMapping("/{id}")
    public Result<WorkRecord> getById(
            @Parameter(description = "工时记录ID") @PathVariable Long id) {
        WorkRecord workRecord = workRecordService.getById(id);
        if (workRecord != null) {
            return Result.success("查询成功", workRecord);
        }
        return Result.error("工时记录不存在");
    }

    /**
     * 查询所有工时记录
     */
    @Operation(summary = "查询所有工时记录")
    @GetMapping("/list")
    public Result<List<WorkRecord>> list() {
        List<WorkRecord> list = workRecordService.list();
        return Result.success("查询成功", list);
    }

    /**
     * 根据用户ID查询工时记录
     */
    @Operation(summary = "根据用户ID查询工时记录")
    @GetMapping("/user/{userId}")
    public Result<List<WorkRecord>> getByUserId(
            @Parameter(description = "用户ID") @PathVariable Long userId) {
        QueryWrapper<WorkRecord> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        List<WorkRecord> list = workRecordService.list(queryWrapper);
        return Result.success("查询成功", list);
    }

    /**
     * 根据项目ID查询工时记录
     */
    @Operation(summary = "根据项目ID查询工时记录")
    @GetMapping("/project/{projectId}")
    public Result<List<WorkRecord>> getByProjectId(
            @Parameter(description = "项目ID") @PathVariable Long projectId) {
        QueryWrapper<WorkRecord> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("project_id", projectId);
        List<WorkRecord> list = workRecordService.list(queryWrapper);
        return Result.success("查询成功", list);
    }

    /**
     * 根据任务ID查询工时记录
     */
    @Operation(summary = "根据任务ID查询工时记录")
    @GetMapping("/task/{taskId}")
    public Result<List<WorkRecord>> getByTaskId(
            @Parameter(description = "任务ID") @PathVariable Long taskId) {
        QueryWrapper<WorkRecord> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("task_id", taskId);
        List<WorkRecord> list = workRecordService.list(queryWrapper);
        return Result.success("查询成功", list);
    }

    /**
     * 根据状态查询工时记录
     */
    @Operation(summary = "根据状态查询工时记录")
    @GetMapping("/status/{status}")
    public Result<List<WorkRecord>> getByStatus(
            @Parameter(description = "状态") @PathVariable Integer status) {
        QueryWrapper<WorkRecord> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("status", status);
        List<WorkRecord> list = workRecordService.list(queryWrapper);
        return Result.success("查询成功", list);
    }

    /**
     * 分页查询工时记录
     */
    @Operation(summary = "分页查询工时记录")
    @GetMapping("/page")
    public Result<Page<WorkRecord>> page(
            @Parameter(description = "当前页") @RequestParam(defaultValue = "1") Integer current,
            @Parameter(description = "每页条数") @RequestParam(defaultValue = "10") Integer size,
            @Parameter(description = "用户ID（可选）") @RequestParam(required = false) Long userId,
            @Parameter(description = "项目ID（可选）") @RequestParam(required = false) Long projectId,
            @Parameter(description = "任务ID（可选）") @RequestParam(required = false) Long taskId,
            @Parameter(description = "状态（可选）") @RequestParam(required = false) Integer status,
            @Parameter(description = "工作类型（可选）") @RequestParam(required = false) Integer workType) {
        Page<WorkRecord> page = new Page<>(current, size);
        QueryWrapper<WorkRecord> queryWrapper = new QueryWrapper<>();

        if (userId != null) {
            queryWrapper.eq("user_id", userId);
        }
        if (projectId != null) {
            queryWrapper.eq("project_id", projectId);
        }
        if (taskId != null) {
            queryWrapper.eq("task_id", taskId);
        }
        if (status != null) {
            queryWrapper.eq("status", status);
        }
        if (workType != null) {
            queryWrapper.eq("work_type", workType);
        }

        Page<WorkRecord> result = workRecordService.page(page, queryWrapper);
        return Result.success("查询成功", result);
    }

    /**
     * 分页查询当前登录用户自己上报的工时
     */
    @Operation(summary = "分页查询当前登录用户自己上报的工时")
    @GetMapping("/my/page")
    public Result<Page<WorkRecord>> getMyPage(
            @Parameter(description = "当前页") @RequestParam(defaultValue = "1") Integer current,
            @Parameter(description = "每页条数") @RequestParam(defaultValue = "10") Integer size,
            @Parameter(description = "项目ID（可选）") @RequestParam(required = false) Long projectId,
            @Parameter(description = "任务ID（可选）") @RequestParam(required = false) Long taskId,
            @Parameter(description = "状态（可选）") @RequestParam(required = false) Integer status,
            @Parameter(description = "工作类型（可选）") @RequestParam(required = false) Integer workType) {
        Long userId = SecurityUtil.getCurrentUserId();
        if (userId == null) {
            return Result.error("用户未登录");
        }
        Page<WorkRecord> page = new Page<>(current, size);
        QueryWrapper<WorkRecord> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);

        if (projectId != null) {
            queryWrapper.eq("project_id", projectId);
        }
        if (taskId != null) {
            queryWrapper.eq("task_id", taskId);
        }
        if (status != null) {
            queryWrapper.eq("status", status);
        }
        if (workType != null) {
            queryWrapper.eq("work_type", workType);
        }

        Page<WorkRecord> result = workRecordService.page(page, queryWrapper);
        return Result.success("查询成功", result);
    }

    /**
     * 按项目按周统计工时
     */
    @Operation(summary = "按项目按周统计工时")
    @GetMapping("/stats/project-weekly")
    public Result<ProjectWeeklyStatsVO> getProjectWeeklyStats(
            @Parameter(description = "项目ID") @RequestParam Long projectId,
            @Parameter(description = "年月，格式 YYYY-MM") @RequestParam String month) {
        ProjectWeeklyStatsVO stats = workRecordService.getProjectWeeklyStats(projectId, month);
        return Result.success("查询成功", stats);
    }

    /**
     * 统计工时排名前N的项目
     */
    @Operation(summary = "统计工时排名前N的项目")
    @GetMapping("/stats/top-projects")
    public Result<TopProjectsStatsVO> getTopProjectsStats(
            @Parameter(description = "年月，格式 YYYY-MM") @RequestParam String month,
            @Parameter(description = "返回数量，默认5") @RequestParam(defaultValue = "5") Integer limit) {
        TopProjectsStatsVO stats = workRecordService.getTopProjectsStats(month, limit);
        return Result.success("查询成功", stats);
    }

    /**
     * 导出工时记录
     */
    @Operation(summary = "导出工时记录")
    @GetMapping("/export")
    public void export(
            @Parameter(description = "用户ID（可选）") @RequestParam(required = false) Long userId,
            @Parameter(description = "项目ID（可选）") @RequestParam(required = false) Long projectId,
            @Parameter(description = "开始日期（可选）") @RequestParam(required = false) String startDate,
            @Parameter(description = "结束日期（可选）") @RequestParam(required = false) String endDate,
            HttpServletResponse response) {
        try {
            WorkRecordExportQuery query = new WorkRecordExportQuery(userId, projectId, startDate, endDate);
            List<WorkRecordExportVO> list = workRecordService.getExportData(query);

            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setCharacterEncoding("utf-8");
            String fileName = URLEncoder.encode("工时记录", StandardCharsets.UTF_8).replaceAll("\\+", "%20");
            response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");

            EasyExcel.write(response.getOutputStream(), WorkRecordExportVO.class)
                    .sheet("工时记录")
                    .doWrite(list);
        } catch (IOException e) {
            throw new RuntimeException("导出失败", e);
        }
    }
}
