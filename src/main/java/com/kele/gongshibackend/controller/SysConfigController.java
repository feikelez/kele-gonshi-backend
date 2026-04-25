package com.kele.gongshibackend.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kele.gongshibackend.common.Result;
import com.kele.gongshibackend.entity.SysConfig;
import com.kele.gongshibackend.service.SysConfigService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 系统配置控制器
 *
 * @author kele
 * @since 2026-04-05
 */
@Tag(name = "系统配置管理", description = "系统配置增删改查接口")
@RestController
@RequestMapping("/sys-config")
public class SysConfigController {

    @Autowired
    private SysConfigService sysConfigService;

    /**
     * 新增系统配置
     */
    @Operation(summary = "新增系统配置")
    @PostMapping
    public Result<SysConfig> add(@RequestBody SysConfig sysConfig) {
        boolean success = sysConfigService.save(sysConfig);
        if (success) {
            return Result.success("新增成功", sysConfig);
        }
        return Result.error("新增失败");
    }

    /**
     * 根据ID删除系统配置
     */
    @Operation(summary = "根据ID删除系统配置")
    @DeleteMapping("/{id}")
    public Result<Void> delete(
            @Parameter(description = "系统配置ID") @PathVariable Long id) {
        boolean success = sysConfigService.removeById(id);
        if (success) {
            return Result.success("删除成功");
        }
        return Result.error("删除失败");
    }

    /**
     * 批量删除系统配置
     */
    @Operation(summary = "批量删除系统配置")
    @DeleteMapping("/batch")
    public Result<Void> deleteBatch(@RequestBody List<Long> ids) {
        boolean success = sysConfigService.removeByIds(ids);
        if (success) {
            return Result.success("批量删除成功");
        }
        return Result.error("批量删除失败");
    }

    /**
     * 修改系统配置
     */
    @Operation(summary = "修改系统配置")
    @PutMapping
    public Result<SysConfig> update(@RequestBody SysConfig sysConfig) {
        boolean success = sysConfigService.updateById(sysConfig);
        if (success) {
            return Result.success("修改成功", sysConfig);
        }
        return Result.error("修改失败");
    }

    /**
     * 根据ID查询系统配置
     */
    @Operation(summary = "根据ID查询系统配置")
    @GetMapping("/{id}")
    public Result<SysConfig> getById(
            @Parameter(description = "系统配置ID") @PathVariable Long id) {
        SysConfig sysConfig = sysConfigService.getById(id);
        if (sysConfig != null) {
            return Result.success("查询成功", sysConfig);
        }
        return Result.error("系统配置不存在");
    }

    /**
     * 查询所有系统配置
     */
    @Operation(summary = "查询所有系统配置")
    @GetMapping("/list")
    public Result<List<SysConfig>> list() {
        List<SysConfig> list = sysConfigService.list();
        return Result.success("查询成功", list);
    }

    /**
     * 根据配置键查询系统配置
     */
    @Operation(summary = "根据配置键查询系统配置")
    @GetMapping("/key/{configKey}")
    public Result<SysConfig> getByConfigKey(
            @Parameter(description = "配置键") @PathVariable String configKey) {
        QueryWrapper<SysConfig> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("config_key", configKey);
        SysConfig sysConfig = sysConfigService.getOne(queryWrapper);
        if (sysConfig != null) {
            return Result.success("查询成功", sysConfig);
        }
        return Result.error("系统配置不存在");
    }

    /**
     * 分页查询系统配置
     */
    @Operation(summary = "分页查询系统配置")
    @GetMapping("/page")
    public Result<Page<SysConfig>> page(
            @Parameter(description = "当前页") @RequestParam(defaultValue = "1") Integer current,
            @Parameter(description = "每页条数") @RequestParam(defaultValue = "10") Integer size,
            @Parameter(description = "配置键（可选）") @RequestParam(required = false) String configKey) {
        Page<SysConfig> page = new Page<>(current, size);
        QueryWrapper<SysConfig> queryWrapper = new QueryWrapper<>();

        if (configKey != null && !configKey.isEmpty()) {
            queryWrapper.like("config_key", configKey);
        }

        Page<SysConfig> result = sysConfigService.page(page, queryWrapper);
        return Result.success("查询成功", result);
    }
}
