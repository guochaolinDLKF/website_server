package com.ydzz.admin.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.ydzz.admin.config.AdminStpUtil;
import com.ydzz.admin.entity.AdminPermission;
import com.ydzz.admin.service.AdminPermissionService;
import com.ydzz.admin.vo.MenuNode;
import com.ydzz.common.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 后台权限/菜单接口。
 *
 * @author WebsiteServer
 * @since 1.0.0
 */
@RestController
@RequestMapping("/api/admin/permission")
@Tag(name = "后台-权限菜单")
public class AdminPermissionController {

    private final AdminPermissionService permissionService;

    public AdminPermissionController(AdminPermissionService permissionService) {
        this.permissionService = permissionService;
    }

    @Operation(summary = "全量权限菜单树")
    @SaCheckPermission(type = AdminStpUtil.TYPE, value = "permission:tree")
    @GetMapping("/tree")
    public Result<List<MenuNode>> tree() {
        return Result.success(permissionService.fullTree());
    }

    @Operation(summary = "全量权限列表")
    @SaCheckPermission(type = AdminStpUtil.TYPE, value = "permission:tree")
    @GetMapping("/list")
    public Result<List<AdminPermission>> list() {
        return Result.success(permissionService.listAll());
    }
}
