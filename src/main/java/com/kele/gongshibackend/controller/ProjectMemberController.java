package com.kele.gongshibackend.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kele.gongshibackend.common.Result;
import com.kele.gongshibackend.dto.ProjectMemberAssignRequest;
import com.kele.gongshibackend.entity.ProjectMember;
import com.kele.gongshibackend.service.ProjectMemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

/**
 * 项目成员控制器
 *
 * @author kele
 * @since 2026-04-05
 */
@Tag(name = "项目成员管理", description = "项目成员增删改查接口")
@RestController
@RequestMapping("/project-member")
public class ProjectMemberController {

    @Autowired
    private ProjectMemberService projectMemberService;

    /**
     * 新增项目成员
     */
    @Operation(summary = "新增项目成员")
    @PostMapping
    public Result<ProjectMember> add(@RequestBody ProjectMember projectMember) {
        boolean success = projectMemberService.save(projectMember);
        if (success) {
            return Result.success("新增成功", projectMember);
        }
        return Result.error("新增失败");
    }

    /**
     * 根据ID删除项目成员
     */
    @Operation(summary = "根据ID删除项目成员")
    @DeleteMapping("/{id}")
    public Result<Void> delete(
            @Parameter(description = "项目成员ID") @PathVariable Long id) {
        boolean success = projectMemberService.removeById(id);
        if (success) {
            return Result.success("删除成功");
        }
        return Result.error("删除失败");
    }

    /**
     * 批量删除项目成员
     */
    @Operation(summary = "批量删除项目成员")
    @DeleteMapping("/batch")
    public Result<Void> deleteBatch(@RequestBody List<Long> ids) {
        boolean success = projectMemberService.removeByIds(ids);
        if (success) {
            return Result.success("批量删除成功");
        }
        return Result.error("批量删除失败");
    }

    /**
     * 修改项目成员
     */
    @Operation(summary = "修改项目成员")
    @PutMapping
    public Result<ProjectMember> update(@RequestBody ProjectMember projectMember) {
        boolean success = projectMemberService.updateById(projectMember);
        if (success) {
            return Result.success("修改成功", projectMember);
        }
        return Result.error("修改失败");
    }

    /**
     * 根据ID查询项目成员
     */
    @Operation(summary = "根据ID查询项目成员")
    @GetMapping("/{id}")
    public Result<ProjectMember> getById(
            @Parameter(description = "项目成员ID") @PathVariable Long id) {
        ProjectMember projectMember = projectMemberService.getById(id);
        if (projectMember != null) {
            return Result.success("查询成功", projectMember);
        }
        return Result.error("项目成员不存在");
    }

    /**
     * 查询所有项目成员
     */
    @Operation(summary = "查询所有项目成员")
    @GetMapping("/list")
    public Result<List<ProjectMember>> list() {
        List<ProjectMember> list = projectMemberService.list();
        return Result.success("查询成功", list);
    }

    /**
     * 根据项目ID查询项目成员
     */
    @Operation(summary = "根据项目ID查询项目成员")
    @GetMapping("/project/{projectId}")
    public Result<List<ProjectMember>> getByProjectId(
            @Parameter(description = "项目ID") @PathVariable Long projectId) {
        QueryWrapper<ProjectMember> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("project_id", projectId);
        List<ProjectMember> list = projectMemberService.list(queryWrapper);
        return Result.success("查询成功", list);
    }

    /**
     * 根据用户ID查询项目成员
     */
    @Operation(summary = "根据用户ID查询项目成员")
    @GetMapping("/user/{userId}")
    public Result<List<ProjectMember>> getByUserId(
            @Parameter(description = "用户ID") @PathVariable Long userId) {
        QueryWrapper<ProjectMember> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        List<ProjectMember> list = projectMemberService.list(queryWrapper);
        return Result.success("查询成功", list);
    }

    /**
     * 分页查询项目成员
     */
    @Operation(summary = "分页查询项目成员")
    @GetMapping("/page")
    public Result<Page<ProjectMember>> page(
            @Parameter(description = "当前页") @RequestParam(defaultValue = "1") Integer current,
            @Parameter(description = "每页条数") @RequestParam(defaultValue = "10") Integer size,
            @Parameter(description = "项目ID（可选）") @RequestParam(required = false) Long projectId,
            @Parameter(description = "用户ID（可选）") @RequestParam(required = false) Long userId) {
        Page<ProjectMember> page = new Page<>(current, size);
        QueryWrapper<ProjectMember> queryWrapper = new QueryWrapper<>();

        if (projectId != null) {
            queryWrapper.eq("project_id", projectId);
        }
        if (userId != null) {
            queryWrapper.eq("user_id", userId);
        }

        Page<ProjectMember> result = projectMemberService.page(page, queryWrapper);
        return Result.success("查询成功", result);
    }

    /**
     * 分配项目成员（原子操作）
     * 先删除项目所有成员关联，再新增新分配的成员
     */
    @Operation(summary = "分配项目成员（原子操作）")
    @PostMapping("/assign")
    public Result<Void> assign(@Valid @RequestBody ProjectMemberAssignRequest request) {
        projectMemberService.assignMembers(request.getProjectId(), request.getUserIds());
        return Result.success("分配成功");
    }
}
