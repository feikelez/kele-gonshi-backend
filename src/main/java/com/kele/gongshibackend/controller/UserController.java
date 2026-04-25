package com.kele.gongshibackend.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kele.gongshibackend.annotation.RequireRole;
import com.kele.gongshibackend.common.Result;
import com.kele.gongshibackend.entity.User;
import com.kele.gongshibackend.service.UserService;
import com.kele.gongshibackend.vo.UserVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户控制器
 *
 * @author kele
 * @since 2026-04-05
 */
@Tag(name = "用户管理", description = "用户增删改查接口")
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 新增用户
     */
    @Operation(summary = "新增用户")
    @PostMapping
    public Result<User> add(@RequestBody User user) {
        boolean success = userService.save(user);
        if (success) {
            return Result.success("新增成功", user);
        }
        return Result.error("新增失败");
    }

    /**
     * 根据ID删除用户
     */
    @Operation(summary = "根据ID删除用户")
    @DeleteMapping("/{id}")
    public Result<Void> delete(
            @Parameter(description = "用户ID") @PathVariable Long id) {
        boolean success = userService.removeById(id);
        if (success) {
            return Result.success("删除成功");
        }
        return Result.error("删除失败");
    }

    /**
     * 批量删除用户
     */
    @Operation(summary = "批量删除用户")
    @DeleteMapping("/batch")
    public Result<Void> deleteBatch(@RequestBody List<Long> ids) {
        boolean success = userService.removeByIds(ids);
        if (success) {
            return Result.success("批量删除成功");
        }
        return Result.error("批量删除失败");
    }

    /**
     * 修改用户
     */
    @Operation(summary = "修改用户")
    @PutMapping
    public Result<User> update(@RequestBody User user) {
        boolean success = userService.updateById(user);
        if (success) {
            return Result.success("修改成功", user);
        }
        return Result.error("修改失败");
    }

    /**
     * 根据ID查询用户
     */
    @Operation(summary = "根据ID查询用户")
    @GetMapping("/{id}")
    @RequireRole("ADMIN")
    public Result<User> getById(
            @Parameter(description = "用户ID") @PathVariable Long id) {
        User user = userService.getById(id);
        if (user != null) {
            return Result.success("查询成功", user);
        }
        return Result.error("用户不存在");
    }


    /**
     * 查询所有用户
     */
    @Operation(summary = "查询所有用户")
    @GetMapping("/list")
    public Result<List<User>> listVO() {
        List<User> list = userService.list();
        return Result.success("查询成功", list);
    }

    /**
     * 查询所有用户
     */
    @Operation(summary = "查询所有用户")
    @GetMapping("/list/vo")
    public Result<List<UserVO>> list() {
        List<UserVO> list = userService.list().stream()
                .map(user -> new UserVO(user.getId(), user.getRealName()))
                .collect(Collectors.toList());
        return Result.success("查询成功", list);
    }

    /**
     * 根据用户名查询用户
     */
    @Operation(summary = "根据用户名查询用户")
    @GetMapping("/username/{username}")
    public Result<User> getByUsername(
            @Parameter(description = "用户名") @PathVariable String username) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username);
        User user = userService.getOne(queryWrapper);
        if (user != null) {
            return Result.success("查询成功", user);
        }
        return Result.error("用户不存在");
    }

    /**
     * 分页查询用户
     */
    @Operation(summary = "分页查询用户")
    @GetMapping("/page")
    public Result<Page<User>> page(
            @Parameter(description = "当前页") @RequestParam(defaultValue = "1") Integer current,
            @Parameter(description = "每页条数") @RequestParam(defaultValue = "10") Integer size,
            @Parameter(description = "用户名（可选）") @RequestParam(required = false) String username,
            @Parameter(description = "状态（可选）") @RequestParam(required = false) Integer status) {
        Page<User> page = new Page<>(current, size);
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();

        if (username != null && !username.isEmpty()) {
            queryWrapper.like("username", username);
        }
        if (status != null) {
            queryWrapper.eq("status", status);
        }

        Page<User> result = userService.page(page, queryWrapper);
        return Result.success("查询成功", result);
    }
}
