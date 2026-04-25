package com.kele.gongshibackend.controller;

import com.kele.gongshibackend.annotation.RequireRole;
import com.kele.gongshibackend.common.Result;
import com.kele.gongshibackend.service.SysUserRoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户角色关联表 Controller
 */
@Tag(name = "用户角色管理", description = "用户角色关联管理相关接口")
@RestController
@RequestMapping("/userRole")
public class SysUserRoleController {

    @Resource
    private SysUserRoleService sysUserRoleService;

    /**
     * 为用户分配角色
     */
    @Operation(summary = "为用户分配角色", description = "为指定用户分配一个或多个角色，会清除用户原有角色")
    @PostMapping("/assign")
    @RequireRole("ADMIN")
    public Result<String> assignRoles(
            @Parameter(description = "用户ID") @RequestParam Long userId, 
            @Parameter(description = "角色ID列表") @RequestBody List<Long> roleIds) {
        boolean success = sysUserRoleService.assignRoles(userId, roleIds);
        return success ? Result.success("分配成功") : Result.error("分配失败");
    }

    /**
     * 获取用户的角色ID列表
     */
    @Operation(summary = "获取用户的角色ID列表", description = "查询指定用户拥有的所有角色ID")
    @GetMapping("/roleIds/{userId}")
    @RequireRole("ADMIN")
    public Result<List<Long>> getRoleIdsByUserId(
            @Parameter(description = "用户ID") @PathVariable Long userId) {
        List<Long> roleIds = sysUserRoleService.getRoleIdsByUserId(userId);
        return Result.success(roleIds);
    }

    /**
     * 删除用户的所有角色
     */
    @Operation(summary = "删除用户的所有角色", description = "删除指定用户的所有角色关联")
    @DeleteMapping("/user/{userId}")
    @RequireRole("ADMIN")
    public Result<String> removeUserRoles(
            @Parameter(description = "用户ID") @PathVariable Long userId) {
        boolean success = sysUserRoleService.removeUserRoles(userId);
        return success ? Result.success("删除成功") : Result.error("删除失败");
    }

    /**
     * 删除用户角色关联
     */
    @Operation(summary = "删除用户角色关联", description = "根据关联ID删除指定的用户角色关系")
    @DeleteMapping("/{id}")
    @RequireRole("ADMIN")
    public Result<String> deleteUserRole(
            @Parameter(description = "用户角色关联ID") @PathVariable Long id) {
        boolean success = sysUserRoleService.removeById(id);
        return success ? Result.success("删除成功") : Result.error("删除失败");
    }

    /**
     * 批量删除用户角色关联
     */
    @Operation(summary = "批量删除用户角色关联", description = "批量删除多个用户角色关联关系")
    @DeleteMapping("/batch")
    @RequireRole("ADMIN")
    public Result<String> deleteUserRoleBatch(
            @Parameter(description = "用户角色关联ID列表") @RequestBody List<Long> ids) {
        boolean success = sysUserRoleService.removeByIds(ids);
        return success ? Result.success("批量删除成功") : Result.error("批量删除失败");
    }
}
