package com.ydzz.admin.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.ydzz.admin.common.PageResult;
import com.ydzz.admin.config.AdminStpUtil;
import com.ydzz.admin.entity.AdminUser;
import com.ydzz.admin.entity.SysConfig;
import com.ydzz.admin.log.OperationLog;
import com.ydzz.admin.mapper.AdminUserMapper;
import com.ydzz.admin.service.SysConfigService;
import com.ydzz.common.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

/**
 * 系统配置接口。
 *
 * @author WebsiteServer
 * @since 1.0.0
 */
@RestController
@RequestMapping("/api/admin/sys-config")
@Tag(name = "后台-系统配置")
public class SysConfigController {

    private final SysConfigService sysConfigService;
    private final AdminUserMapper adminUserMapper;

    public SysConfigController(SysConfigService sysConfigService, AdminUserMapper adminUserMapper) {
        this.sysConfigService = sysConfigService;
        this.adminUserMapper = adminUserMapper;
    }

    @Operation(summary = "配置分页")
    @SaCheckPermission(type = AdminStpUtil.TYPE, value = "config:list")
    @GetMapping("/page")
    public Result<PageResult<SysConfig>> page(@RequestParam(defaultValue = "1") long current,
                                              @RequestParam(defaultValue = "10") long size,
                                              @RequestParam(required = false) String keyword,
                                              @RequestParam(required = false) String group) {
        return Result.success(PageResult.of(sysConfigService.page(current, size, keyword, group)));
    }

    @Operation(summary = "新增/编辑配置")
    @SaCheckPermission(type = AdminStpUtil.TYPE, value = "config:edit")
    @OperationLog(module = "系统配置", operation = "保存配置")
    @PostMapping
    public Result<Long> save(@RequestBody SysConfig req) {
        return Result.success("保存成功", sysConfigService.save(req, currentUsername()));
    }

    @Operation(summary = "删除配置")
    @SaCheckPermission(type = AdminStpUtil.TYPE, value = "config:edit")
    @OperationLog(module = "系统配置", operation = "删除配置")
    @DeleteMapping("/{id}")
    public Result<String> remove(@PathVariable Long id) {
        sysConfigService.removeById(id);
        return Result.success("删除成功", "ok");
    }

    private String currentUsername() {
        Object loginId = AdminStpUtil.getLoginIdDefaultNull();
        if (loginId == null) {
            return null;
        }
        AdminUser user = adminUserMapper.selectById(Long.valueOf(loginId.toString()));
        return user == null ? null : user.getUsername();
    }
}
