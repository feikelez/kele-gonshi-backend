package com.kele.gongshibackend.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kele.gongshibackend.common.Result;
import com.kele.gongshibackend.entity.ProjectMember;
import com.kele.gongshibackend.entity.Task;
import com.kele.gongshibackend.service.ProjectMemberService;
import com.kele.gongshibackend.service.TaskService;
import com.kele.gongshibackend.util.SecurityUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 任务控制器
 *
 * @author kele
 * @since 2026-04-05
 */
@Tag(name = "任务管理", description = "任务增删改查接口")
@RestController
@RequestMapping("/task")
public class TaskController {

    @Autowired
    private TaskService taskService;

    @Autowired
    private ProjectMemberService projectMemberService;

    /**
     * 新增任务
     */
    @Operation(summary = "新增任务")
    @PostMapping
    public Result<Task> add(@RequestBody Task task) {
        boolean success = taskService.save(task);
        if (success) {
            return Result.success("新增成功", task);
        }
        return Result.error("新增失败");
    }

    /**
     * 根据ID删除任务
     */
    @Operation(summary = "根据ID删除任务")
    @DeleteMapping("/{id}")
    public Result<Void> delete(
            @Parameter(description = "任务ID") @PathVariable Long id) {
        boolean success = taskService.removeById(id);
        if (success) {
            return Result.success("删除成功");
        }
        return Result.error("删除失败");
    }

    /**
     * 批量删除任务
     */
    @Operation(summary = "批量删除任务")
    @DeleteMapping("/batch")
    public Result<Void> deleteBatch(@RequestBody List<Long> ids) {
        boolean success = taskService.removeByIds(ids);
        if (success) {
            return Result.success("批量删除成功");
        }
        return Result.error("批量删除失败");
    }

    /**
     * 修改任务
     */
    @Operation(summary = "修改任务")
    @PutMapping
    public Result<Task> update(@RequestBody Task task) {
        boolean success = taskService.updateById(task);
        if (success) {
            return Result.success("修改成功", task);
        }
        return Result.error("修改失败");
    }

    /**
     * 根据ID查询任务
     */
    @Operation(summary = "根据ID查询任务")
    @GetMapping("/{id}")
    public Result<Task> getById(
            @Parameter(description = "任务ID") @PathVariable Long id) {
        Task task = taskService.getById(id);
        if (task != null) {
            return Result.success("查询成功", task);
        }
        return Result.error("任务不存在");
    }

    /**
     * 查询所有任务
     */
    @Operation(summary = "查询所有任务")
    @GetMapping("/list")
    public Result<List<Task>> list() {
        List<Task> list = taskService.list();
        return Result.success("查询成功", list);
    }

    /**
     * 根据项目ID查询任务
     */
    @Operation(summary = "根据项目ID查询任务")
    @GetMapping("/project/{projectId}")
    public Result<List<Task>> getByProjectId(
            @Parameter(description = "项目ID") @PathVariable Long projectId) {
        QueryWrapper<Task> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("project_id", projectId);
        List<Task> list = taskService.list(queryWrapper);
        return Result.success("查询成功", list);
    }

    /**
     * 根据指派人ID查询任务
     */
    @Operation(summary = "根据指派人ID查询任务")
    @GetMapping("/assignee/{assigneeId}")
    public Result<List<Task>> getByAssigneeId(
            @Parameter(description = "指派人ID") @PathVariable Long assigneeId) {
        QueryWrapper<Task> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("assignee_id", assigneeId);
        List<Task> list = taskService.list(queryWrapper);
        return Result.success("查询成功", list);
    }

    /**
     * 分页查询任务
     */
    @Operation(summary = "分页查询任务")
    @GetMapping("/page")
    public Result<Page<Task>> page(
            @Parameter(description = "当前页") @RequestParam(defaultValue = "1") Integer current,
            @Parameter(description = "每页条数") @RequestParam(defaultValue = "10") Integer size,
            @Parameter(description = "任务名称（可选）") @RequestParam(required = false) String taskName,
            @Parameter(description = "项目ID（可选）") @RequestParam(required = false) Long projectId,
            @Parameter(description = "指派人ID（可选）") @RequestParam(required = false) Long assigneeId,
            @Parameter(description = "状态（可选）") @RequestParam(required = false) Integer status) {
        Page<Task> page = new Page<>(current, size);
        QueryWrapper<Task> queryWrapper = new QueryWrapper<>();

        if (taskName != null && !taskName.isEmpty()) {
            queryWrapper.like("task_name", taskName);
        }
        if (projectId != null) {
            queryWrapper.eq("project_id", projectId);
        }
        if (assigneeId != null) {
            queryWrapper.eq("assignee_id", assigneeId);
        }
        if (status != null) {
            queryWrapper.eq("status", status);
        }

        Page<Task> result = taskService.page(page, queryWrapper);
        return Result.success("查询成功", result);
    }

    /**
     * 获取当前登录用户参与的项目相关的任务分页列表
     */
    @Operation(summary = "获取当前登录用户参与的项目相关的任务分页列表")
    @GetMapping("/my/page")
    public Result<Page<Task>> getMyTasksPage(
            @Parameter(description = "当前页") @RequestParam(defaultValue = "1") Integer current,
            @Parameter(description = "每页条数") @RequestParam(defaultValue = "10") Integer size,
            @Parameter(description = "任务名称（可选）") @RequestParam(required = false) String taskName,
            @Parameter(description = "项目ID（可选）") @RequestParam(required = false) Long projectId,
            @Parameter(description = "状态（可选）") @RequestParam(required = false) Integer status) {
        Long userId = SecurityUtil.getCurrentUserId();
        if (userId == null) {
            return Result.error("用户未登录");
        }

        // 先查询该用户参与的所有项目成员记录
        QueryWrapper<ProjectMember> memberQueryWrapper = new QueryWrapper<>();
        memberQueryWrapper.eq("user_id", userId);
        List<ProjectMember> members = projectMemberService.list(memberQueryWrapper);

        if (members.isEmpty()) {
            // 该用户没有参与任何项目，返回空分页结果
            Page<Task> emptyPage = new Page<>(current, size);
            return Result.success("查询成功", emptyPage);
        }

        // 获取该项目ID列表
        List<Long> projectIds = members.stream()
                .map(ProjectMember::getProjectId)
                .collect(Collectors.toList());

        // 分页查询任务
        Page<Task> page = new Page<>(current, size);
        QueryWrapper<Task> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("project_id", projectIds);

        if (taskName != null && !taskName.isEmpty()) {
            queryWrapper.like("task_name", taskName);
        }
        if (projectId != null) {
            queryWrapper.eq("project_id", projectId);
        }
        if (status != null) {
            queryWrapper.eq("status", status);
        }

        // 按创建时间倒序
        queryWrapper.orderByDesc("create_time");

        Page<Task> result = taskService.page(page, queryWrapper);
        return Result.success("查询成功", result);
    }
}
