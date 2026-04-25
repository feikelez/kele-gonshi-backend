package com.kele.gongshibackend.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kele.gongshibackend.annotation.RequireRole;
import com.kele.gongshibackend.common.Result;
import com.kele.gongshibackend.entity.SysRole;
import com.kele.gongshibackend.service.SysRoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * 角色表 Controller
 */
@Tag(name = "角色管理", description = "角色管理相关接口")
@RestController
@RequestMapping("/role")
public class SysRoleController {

    @Resource
    private SysRoleService sysRoleService;

    /**
     * 新增角色
     */
    @Operation(summary = "新增角色", description = "创建一个新的系统角色")
    @PostMapping
    @RequireRole("ADMIN")
    public Result<String> addRole(
            @Parameter(description = "角色信息") @RequestBody @Validated SysRole sysRole) {
        boolean success = sysRoleService.save(sysRole);
        return success ? Result.success("新增成功") : Result.error("新增失败");
    }

    /**
     * 修改角色
     */
    @Operation(summary = "修改角色", description = "更新现有角色信息")
    @PutMapping
    @RequireRole("ADMIN")
    public Result<String> updateRole(
            @Parameter(description = "角色信息") @RequestBody @Validated SysRole sysRole) {
        boolean success = sysRoleService.updateById(sysRole);
        return success ? Result.success("修改成功") : Result.error("修改失败");
    }

    /**
     * 删除角色
     */
    @Operation(summary = "删除角色", description = "根据ID删除指定角色")
    @DeleteMapping("/{id}")
    @RequireRole("ADMIN")
    public Result<String> deleteRole(
            @Parameter(description = "角色ID") @PathVariable Long id) {
        boolean success = sysRoleService.removeById(id);
        return success ? Result.success("删除成功") : Result.error("删除失败");
    }

    /**
     * 批量删除角色
     */
    @Operation(summary = "批量删除角色", description = "批量删除多个角色")
    @DeleteMapping("/batch")
    @RequireRole("ADMIN")
    public Result<String> deleteRoleBatch(
            @Parameter(description = "角色ID列表") @RequestBody List<Long> ids) {
        boolean success = sysRoleService.removeByIds(ids);
        return success ? Result.success("批量删除成功") : Result.error("批量删除失败");
    }

    /**
     * 根据ID查询角色
     */
    @Operation(summary = "根据ID查询角色", description = "通过角色ID获取角色详细信息")
    @GetMapping("/{id}")
    @RequireRole("ADMIN")
    public Result<SysRole> getRoleById(
            @Parameter(description = "角色ID") @PathVariable Long id) {
        SysRole sysRole = sysRoleService.getById(id);
        return sysRole != null ? Result.success(sysRole) : Result.error("查询失败");
    }

    /**
     * 分页查询角色列表
     */
    @Operation(summary = "分页查询角色列表", description = "分页查询角色列表，支持按角色名称模糊查询")
    @GetMapping("/page")
    @RequireRole("ADMIN")
    public Result<Page<SysRole>> getRolePage(
            @Parameter(description = "当前页码") @RequestParam(defaultValue = "1") Long current,
            @Parameter(description = "每页条数") @RequestParam(defaultValue = "10") Long size,
            @Parameter(description = "角色名称（模糊查询）") @RequestParam(required = false) String roleName) {
        Page<SysRole> page = sysRoleService.getRolePage(current, size, roleName);
        return Result.success(page);
    }

    /**
     * 查询所有角色
     */
    @Operation(summary = "查询所有角色", description = "获取系统中的所有角色列表")
    @GetMapping("/list")
    @RequireRole("ADMIN")
    public Result<List<SysRole>> getRoleList() {
        List<SysRole> list = sysRoleService.list();
        return Result.success(list);
    }
}
