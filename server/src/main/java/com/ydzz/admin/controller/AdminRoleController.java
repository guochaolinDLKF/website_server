package com.ydzz.admin.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.ydzz.admin.config.AdminStpUtil;
import com.ydzz.admin.dto.AdminRoleSaveRequest;
import com.ydzz.admin.entity.AdminRole;
import com.ydzz.admin.log.OperationLog;
import com.ydzz.admin.service.AdminRoleService;
import com.ydzz.common.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 后台角色管理接口。
 *
 * @author WebsiteServer
 * @since 1.0.0
 */
@RestController
@RequestMapping("/api/admin/role")
@Tag(name = "后台-角色管理")
public class AdminRoleController {

    private final AdminRoleService roleService;

    public AdminRoleController(AdminRoleService roleService) {
        this.roleService = roleService;
    }

    @Operation(summary = "角色列表")
    @SaCheckPermission(type = AdminStpUtil.TYPE, value = "role:list")
    @GetMapping("/list")
    public Result<List<AdminRole>> list() {
        return Result.success(roleService.listAll());
    }

    @Operation(summary = "新增/编辑角色")
    @SaCheckPermission(type = AdminStpUtil.TYPE, value = "role:list")
    @OperationLog(module = "角色管理", operation = "保存角色")
    @PostMapping
    public Result<Long> save(@Valid @RequestBody AdminRoleSaveRequest req) {
        return Result.success("保存成功", roleService.save(req));
    }

    @Operation(summary = "启用/禁用角色")
    @SaCheckPermission(type = AdminStpUtil.TYPE, value = "role:list")
    @OperationLog(module = "角色管理", operation = "启用/禁用角色")
    @PostMapping("/{id}/status")
    public Result<String> changeStatus(@PathVariable Long id, @RequestParam Integer status) {
        roleService.changeStatus(id, status);
        return Result.success("操作成功", "ok");
    }

    @Operation(summary = "删除角色")
    @SaCheckPermission(type = AdminStpUtil.TYPE, value = "role:list")
    @OperationLog(module = "角色管理", operation = "删除角色")
    @DeleteMapping("/{id}")
    public Result<String> remove(@PathVariable Long id) {
        roleService.removeById(id);
        return Result.success("删除成功", "ok");
    }

    @Operation(summary = "查询角色已分配的权限ID")
    @SaCheckPermission(type = AdminStpUtil.TYPE, value = "role:assign")
    @GetMapping("/{id}/permissions")
    public Result<List<Long>> permissions(@PathVariable Long id) {
        return Result.success(roleService.listPermissionIds(id));
    }

    @Operation(summary = "给角色分配权限")
    @SaCheckPermission(type = AdminStpUtil.TYPE, value = "role:assign")
    @OperationLog(module = "角色管理", operation = "分配权限")
    @PostMapping("/{id}/permissions")
    public Result<String> assign(@PathVariable Long id, @RequestBody List<Long> permissionIds) {
        roleService.assignPermissions(id, permissionIds);
        return Result.success("分配成功", "ok");
    }
}
