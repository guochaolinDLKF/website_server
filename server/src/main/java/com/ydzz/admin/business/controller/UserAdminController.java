package com.ydzz.admin.business.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.ydzz.admin.business.service.UserAdminService;
import com.ydzz.admin.common.PageResult;
import com.ydzz.admin.config.AdminStpUtil;
import com.ydzz.admin.log.OperationLog;
import com.ydzz.common.Result;
import com.ydzz.entity.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * APP 用户管理接口（读 zhouyi，启用/禁用为受控写）。
 *
 * @author WebsiteServer
 * @since 1.0.0
 */
@RestController
@RequestMapping("/api/admin/user")
@Tag(name = "后台-用户管理")
public class UserAdminController {

    private final UserAdminService userAdminService;

    public UserAdminController(UserAdminService userAdminService) {
        this.userAdminService = userAdminService;
    }

    @Operation(summary = "用户分页（手机号脱敏）")
    @SaCheckPermission(type = AdminStpUtil.TYPE, value = "user:list")
    @GetMapping("/page")
    public Result<PageResult<User>> page(@RequestParam(defaultValue = "1") long current,
                                         @RequestParam(defaultValue = "10") long size,
                                         @RequestParam(required = false) String keyword,
                                         @RequestParam(required = false) Integer isVip,
                                         @RequestParam(required = false) Integer status,
                                         @RequestParam(required = false)
                                         @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
                                         @RequestParam(required = false)
                                         @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime) {
        return Result.success(PageResult.of(
                userAdminService.page(current, size, keyword, isVip, status, startTime, endTime)));
    }

    @Operation(summary = "用户详情（聚合业务记录）")
    @SaCheckPermission(type = AdminStpUtil.TYPE, value = "user:detail")
    @GetMapping("/{id}")
    public Result<Map<String, Object>> detail(@PathVariable Long id) {
        return Result.success(userAdminService.detail(id));
    }

    @Operation(summary = "启用/禁用用户")
    @SaCheckPermission(type = AdminStpUtil.TYPE, value = "user:disable")
    @OperationLog(module = "用户管理", operation = "启用/禁用用户")
    @PostMapping("/{id}/status")
    public Result<String> changeStatus(@PathVariable Long id, @RequestParam Integer status) {
        userAdminService.changeStatus(id, status);
        return Result.success("操作成功", "ok");
    }

    @Operation(summary = "导出用户列表（脱敏）")
    @SaCheckPermission(type = AdminStpUtil.TYPE, value = "user:export")
    @OperationLog(module = "用户管理", operation = "导出用户")
    @GetMapping("/export")
    public Result<List<User>> export(@RequestParam(required = false) String keyword,
                                     @RequestParam(required = false) Integer isVip,
                                     @RequestParam(required = false) Integer status) {
        return Result.success(userAdminService.exportList(keyword, isVip, status));
    }
}
