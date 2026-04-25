package com.kele.gongshibackend.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kele.gongshibackend.common.Result;
import com.kele.gongshibackend.entity.ApprovalConfig;
import com.kele.gongshibackend.service.ApprovalConfigService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 审批人配置控制器
 *
 * @author kele
 * @since 2026-04-05
 */
@Tag(name = "审批人配置管理", description = "审批人配置增删改查接口")
@RestController
@RequestMapping("/approval-config")
public class ApprovalConfigController {

    @Autowired
    private ApprovalConfigService approvalConfigService;

    /**
     * 新增审批人配置
     */
    @Operation(summary = "新增审批人配置")
    @PostMapping
    public Result<ApprovalConfig> add(@RequestBody ApprovalConfig approvalConfig) {
        boolean success = approvalConfigService.save(approvalConfig);
        if (success) {
            return Result.success("新增成功", approvalConfig);
        }
        return Result.error("新增失败");
    }

    /**
     * 根据ID删除审批人配置
     */
    @Operation(summary = "根据ID删除审批人配置")
    @DeleteMapping("/{id}")
    public Result<Void> delete(
            @Parameter(description = "审批人配置ID") @PathVariable Long id) {
        boolean success = approvalConfigService.removeById(id);
        if (success) {
            return Result.success("删除成功");
        }
        return Result.error("删除失败");
    }

    /**
     * 批量删除审批人配置
     */
    @Operation(summary = "批量删除审批人配置")
    @DeleteMapping("/batch")
    public Result<Void> deleteBatch(@RequestBody List<Long> ids) {
        boolean success = approvalConfigService.removeByIds(ids);
        if (success) {
            return Result.success("批量删除成功");
        }
        return Result.error("批量删除失败");
    }

    /**
     * 修改审批人配置
     */
    @Operation(summary = "修改审批人配置")
    @PutMapping
    public Result<ApprovalConfig> update(@RequestBody ApprovalConfig approvalConfig) {
        boolean success = approvalConfigService.updateById(approvalConfig);
        if (success) {
            return Result.success("修改成功", approvalConfig);
        }
        return Result.error("修改失败");
    }

    /**
     * 根据ID查询审批人配置
     */
    @Operation(summary = "根据ID查询审批人配置")
    @GetMapping("/{id}")
    public Result<ApprovalConfig> getById(
            @Parameter(description = "审批人配置ID") @PathVariable Long id) {
        ApprovalConfig approvalConfig = approvalConfigService.getById(id);
        if (approvalConfig != null) {
            return Result.success("查询成功", approvalConfig);
        }
        return Result.error("审批人配置不存在");
    }

    /**
     * 查询所有审批人配置
     */
    @Operation(summary = "查询所有审批人配置")
    @GetMapping("/list")
    public Result<List<ApprovalConfig>> list() {
        List<ApprovalConfig> list = approvalConfigService.list();
        return Result.success("查询成功", list);
    }

    /**
     * 根据用户ID查询审批人配置
     */
    @Operation(summary = "根据用户ID查询审批人配置")
    @GetMapping("/user/{userId}")
    public Result<List<ApprovalConfig>> getByUserId(
            @Parameter(description = "用户ID") @PathVariable Long userId) {
        QueryWrapper<ApprovalConfig> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        List<ApprovalConfig> list = approvalConfigService.list(queryWrapper);
        return Result.success("查询成功", list);
    }

    /**
     * 根据审批人ID查询审批人配置
     */
    @Operation(summary = "根据审批人ID查询审批人配置")
    @GetMapping("/approver/{approverId}")
    public Result<List<ApprovalConfig>> getByApproverId(
            @Parameter(description = "审批人ID") @PathVariable Long approverId) {
        QueryWrapper<ApprovalConfig> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("approver_id", approverId);
        List<ApprovalConfig> list = approvalConfigService.list(queryWrapper);
        return Result.success("查询成功", list);
    }

    /**
     * 分页查询审批人配置
     */
    @Operation(summary = "分页查询审批人配置")
    @GetMapping("/page")
    public Result<Page<ApprovalConfig>> page(
            @Parameter(description = "当前页") @RequestParam(defaultValue = "1") Integer current,
            @Parameter(description = "每页条数") @RequestParam(defaultValue = "10") Integer size,
            @Parameter(description = "用户ID（可选）") @RequestParam(required = false) Long userId,
            @Parameter(description = "审批人ID（可选）") @RequestParam(required = false) Long approverId) {
        Page<ApprovalConfig> page = new Page<>(current, size);
        QueryWrapper<ApprovalConfig> queryWrapper = new QueryWrapper<>();

        if (userId != null) {
            queryWrapper.eq("user_id", userId);
        }
        if (approverId != null) {
            queryWrapper.eq("approver_id", approverId);
        }

        Page<ApprovalConfig> result = approvalConfigService.page(page, queryWrapper);
        return Result.success("查询成功", result);
    }
}
