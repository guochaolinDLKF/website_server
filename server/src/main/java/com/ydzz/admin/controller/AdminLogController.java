package com.ydzz.admin.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ydzz.admin.common.PageResult;
import com.ydzz.admin.config.AdminStpUtil;
import com.ydzz.admin.entity.AdminLoginLog;
import com.ydzz.admin.entity.AdminOperationLog;
import com.ydzz.admin.mapper.AdminLoginLogMapper;
import com.ydzz.admin.mapper.AdminOperationLogMapper;
import com.ydzz.common.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 后台日志查询接口。
 *
 * @author WebsiteServer
 * @since 1.0.0
 */
@RestController
@RequestMapping("/api/admin/log")
@Tag(name = "后台-日志")
public class AdminLogController {

    private final AdminLoginLogMapper loginLogMapper;
    private final AdminOperationLogMapper operationLogMapper;

    public AdminLogController(AdminLoginLogMapper loginLogMapper, AdminOperationLogMapper operationLogMapper) {
        this.loginLogMapper = loginLogMapper;
        this.operationLogMapper = operationLogMapper;
    }

    @Operation(summary = "登录日志分页")
    @SaCheckPermission(type = AdminStpUtil.TYPE, value = "log:login")
    @GetMapping("/login")
    public Result<PageResult<AdminLoginLog>> loginLog(@RequestParam(defaultValue = "1") long current,
                                                      @RequestParam(defaultValue = "10") long size,
                                                      @RequestParam(required = false) String username) {
        LambdaQueryWrapper<AdminLoginLog> qw = new LambdaQueryWrapper<>();
        if (StrUtil.isNotBlank(username)) {
            qw.like(AdminLoginLog::getUsername, username);
        }
        qw.orderByDesc(AdminLoginLog::getLoginTime);
        return Result.success(PageResult.of(loginLogMapper.selectPage(new Page<>(current, size), qw)));
    }

    @Operation(summary = "操作日志分页")
    @SaCheckPermission(type = AdminStpUtil.TYPE, value = "log:operation")
    @GetMapping("/operation")
    public Result<PageResult<AdminOperationLog>> operationLog(@RequestParam(defaultValue = "1") long current,
                                                              @RequestParam(defaultValue = "10") long size,
                                                              @RequestParam(required = false) String username,
                                                              @RequestParam(required = false) String module) {
        LambdaQueryWrapper<AdminOperationLog> qw = new LambdaQueryWrapper<>();
        if (StrUtil.isNotBlank(username)) {
            qw.like(AdminOperationLog::getUsername, username);
        }
        if (StrUtil.isNotBlank(module)) {
            qw.eq(AdminOperationLog::getModule, module);
        }
        qw.orderByDesc(AdminOperationLog::getCreateTime);
        return Result.success(PageResult.of(operationLogMapper.selectPage(new Page<>(current, size), qw)));
    }
}
