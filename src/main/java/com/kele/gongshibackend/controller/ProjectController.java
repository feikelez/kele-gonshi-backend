package com.kele.gongshibackend.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kele.gongshibackend.common.Result;
import com.kele.gongshibackend.entity.Project;
import com.kele.gongshibackend.entity.ProjectMember;
import com.kele.gongshibackend.service.ProjectMemberService;
import com.kele.gongshibackend.service.ProjectService;
import com.kele.gongshibackend.util.SecurityUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 项目控制器
 *
 * @author kele
 * @since 2026-04-05
 */
@Tag(name = "项目管理", description = "项目增删改查接口")
@RestController
@RequestMapping("/project")
public class ProjectController {

    @Autowired
    private ProjectService projectService;

    @Autowired
    private ProjectMemberService projectMemberService;

    /**
     * 新增项目
     */
    @Operation(summary = "新增项目")
    @PostMapping
    public Result<Project> add(@RequestBody Project project) {
        boolean success = projectService.save(project);
        if (success) {
            return Result.success("新增成功", project);
        }
        return Result.error("新增失败");
    }

    /**
     * 根据ID删除项目
     */
    @Operation(summary = "根据ID删除项目")
    @DeleteMapping("/{id}")
    public Result<Void> delete(
            @Parameter(description = "项目ID") @PathVariable Long id) {
        boolean success = projectService.removeById(id);
        if (success) {
            return Result.success("删除成功");
        }
        return Result.error("删除失败");
    }

    /**
     * 批量删除项目
     */
    @Operation(summary = "批量删除项目")
    @DeleteMapping("/batch")
    public Result<Void> deleteBatch(@RequestBody List<Long> ids) {
        boolean success = projectService.removeByIds(ids);
        if (success) {
            return Result.success("批量删除成功");
        }
        return Result.error("批量删除失败");
    }

    /**
     * 修改项目
     */
    @Operation(summary = "修改项目")
    @PutMapping
    public Result<Project> update(@RequestBody Project project) {
        boolean success = projectService.updateById(project);
        if (success) {
            return Result.success("修改成功", project);
        }
        return Result.error("修改失败");
    }

    /**
     * 根据ID查询项目
     */
    @Operation(summary = "根据ID查询项目")
    @GetMapping("/{id}")
    public Result<Project> getById(
            @Parameter(description = "项目ID") @PathVariable Long id) {
        Project project = projectService.getById(id);
        if (project != null) {
            return Result.success("查询成功", project);
        }
        return Result.error("项目不存在");
    }

    /**
     * 查询所有项目
     */
    @Operation(summary = "查询所有项目")
    @GetMapping("/list")
    public Result<List<Project>> list() {
        List<Project> list = projectService.list();
        return Result.success("查询成功", list);
    }

    /**
     * 根据项目编号查询项目
     */
    @Operation(summary = "根据项目编号查询项目")
    @GetMapping("/code/{projectCode}")
    public Result<Project> getByProjectCode(
            @Parameter(description = "项目编号") @PathVariable String projectCode) {
        QueryWrapper<Project> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("project_code", projectCode);
        Project project = projectService.getOne(queryWrapper);
        if (project != null) {
            return Result.success("查询成功", project);
        }
        return Result.error("项目不存在");
    }

    /**
     * 根据经理ID查询项目
     */
    @Operation(summary = "根据经理ID查询项目")
    @GetMapping("/manager/{managerId}")
    public Result<List<Project>> getByManagerId(
            @Parameter(description = "经理ID") @PathVariable Long managerId) {
        QueryWrapper<Project> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("manager_id", managerId);
        List<Project> list = projectService.list(queryWrapper);
        return Result.success("查询成功", list);
    }

    /**
     * 根据经理ID分页查询项目
     */
    @Operation(summary = "根据经理ID分页查询项目")
    @GetMapping("/manager/{managerId}/page")
    public Result<Page<Project>> getByManagerIdPage(
            @Parameter(description = "经理ID") @PathVariable Long managerId,
            @Parameter(description = "当前页") @RequestParam(defaultValue = "1") Integer current,
            @Parameter(description = "每页条数") @RequestParam(defaultValue = "10") Integer size,
            @Parameter(description = "项目名称（可选）") @RequestParam(required = false) String projectName,
            @Parameter(description = "状态（可选）") @RequestParam(required = false) Integer status) {
        Page<Project> page = new Page<>(current, size);
        QueryWrapper<Project> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("manager_id", managerId);
        if (projectName != null && !projectName.isEmpty()) {
            queryWrapper.like("project_name", projectName);
        }
        if (status != null) {
            queryWrapper.eq("status", status);
        }
        Page<Project> result = projectService.page(page, queryWrapper);
        return Result.success("查询成功", result);
    }

    /**
     * 获取当前登录用户参与的项目分页列表
     */
    @Operation(summary = "获取当前登录用户参与的项目分页列表")
    @GetMapping("/my/page")
    public Result<Page<Project>> getMyProjectsPage(
            @Parameter(description = "当前页") @RequestParam(defaultValue = "1") Integer current,
            @Parameter(description = "每页条数") @RequestParam(defaultValue = "10") Integer size) {
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
            Page<Project> emptyPage = new Page<>(current, size);
            return Result.success("查询成功", emptyPage);
        }

        // 获取该项目ID列表
        List<Long> projectIds = members.stream()
                .map(ProjectMember::getProjectId)
                .collect(Collectors.toList());

        // 分页查询项目详情
        Page<Project> page = new Page<>(current, size);
        QueryWrapper<Project> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("id", projectIds);

        Page<Project> result = projectService.page(page, queryWrapper);
        return Result.success("查询成功", result);
    }

    /**
     * 分页查询项目
     */
    @Operation(summary = "分页查询项目")
    @GetMapping("/page")
    public Result<Page<Project>> page(
            @Parameter(description = "当前页") @RequestParam(defaultValue = "1") Integer current,
            @Parameter(description = "每页条数") @RequestParam(defaultValue = "10") Integer size,
            @Parameter(description = "项目名称（可选）") @RequestParam(required = false) String projectName,
            @Parameter(description = "状态（可选）") @RequestParam(required = false) Integer status,
            @Parameter(description = "经理ID（可选）") @RequestParam(required = false) Long managerId) {
        Page<Project> page = new Page<>(current, size);
        QueryWrapper<Project> queryWrapper = new QueryWrapper<>();

        if (projectName != null && !projectName.isEmpty()) {
            queryWrapper.like("project_name", projectName);
        }
        if (status != null) {
            queryWrapper.eq("status", status);
        }
        if (managerId != null) {
            queryWrapper.eq("manager_id", managerId);
        }

        Page<Project> result = projectService.page(page, queryWrapper);
        return Result.success("查询成功", result);
    }
}
