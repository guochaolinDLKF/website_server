package com.ydzz.admin.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.ydzz.admin.common.PageResult;
import com.ydzz.admin.config.AdminStpUtil;
import com.ydzz.admin.dto.AdminUserSaveRequest;
import com.ydzz.admin.entity.AdminUser;
import com.ydzz.admin.log.OperationLog;
import com.ydzz.admin.service.AdminUserService;
import com.ydzz.common.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 后台管理员管理接口。
 *
 * @author WebsiteServer
 * @since 1.0.0
 */
@RestController
@RequestMapping("/api/admin/admin-user")
@Tag(name = "后台-管理员管理")
public class AdminUserController {

    private final AdminUserService adminUserService;

    public AdminUserController(AdminUserService adminUserService) {
        this.adminUserService = adminUserService;
    }

    @Operation(summary = "管理员分页")
    @SaCheckPermission(type = AdminStpUtil.TYPE, value = "admin:list")
    @GetMapping("/page")
    public Result<PageResult<AdminUser>> page(@RequestParam(defaultValue = "1") long current,
                                              @RequestParam(defaultValue = "10") long size,
                                              @RequestParam(required = false) String keyword,
                                              @RequestParam(required = false) Integer status) {
        return Result.success(PageResult.of(adminUserService.page(current, size, keyword, status)));
    }

    @Operation(summary = "管理员详情（含角色ID）")
    @SaCheckPermission(type = AdminStpUtil.TYPE, value = "admin:list")
    @GetMapping("/{id}")
    public Result<Map<String, Object>> detail(@PathVariable Long id) {
        AdminUser user = adminUserService.getById(id);
        Map<String, Object> data = new HashMap<>();
        data.put("user", user);
        data.put("roleIds", adminUserService.listRoleIds(id));
        return Result.success(data);
    }

    @Operation(summary = "新增/编辑管理员")
    @SaCheckPermission(type = AdminStpUtil.TYPE, value = "admin:edit")
    @OperationLog(module = "管理员管理", operation = "保存管理员")
    @PostMapping
    public Result<Long> save(@Valid @RequestBody AdminUserSaveRequest req) {
        return Result.success("保存成功", adminUserService.save(req));
    }

    @Operation(summary = "启用/禁用")
    @SaCheckPermission(type = AdminStpUtil.TYPE, value = "admin:edit")
    @OperationLog(module = "管理员管理", operation = "启用/禁用管理员")
    @PostMapping("/{id}/status")
    public Result<String> changeStatus(@PathVariable Long id, @RequestParam Integer status) {
        adminUserService.changeStatus(id, status);
        return Result.success("操作成功", "ok");
    }

    @Operation(summary = "重置密码（无条件重置为系统默认密码）")
    @SaCheckPermission(type = AdminStpUtil.TYPE, value = "admin:edit")
    @OperationLog(module = "管理员管理", operation = "重置密码")
    @PostMapping("/{id}/reset-pwd")
    public Result<String> resetPwd(@PathVariable Long id) {
        adminUserService.resetPassword(id);
        return Result.success("已重置为默认密码：" + AdminUserService.DEFAULT_PASSWORD, AdminUserService.DEFAULT_PASSWORD);
    }

    @Operation(summary = "删除管理员")
    @SaCheckPermission(type = AdminStpUtil.TYPE, value = "admin:edit")
    @OperationLog(module = "管理员管理", operation = "删除管理员")
    @DeleteMapping("/{id}")
    public Result<String> remove(@PathVariable Long id) {
        adminUserService.removeById(id);
        return Result.success("删除成功", "ok");
    }
}
