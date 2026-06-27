package com.ydzz.admin.controller;

import com.ydzz.admin.dto.AdminInfoVo;
import com.ydzz.admin.dto.AdminLoginRequest;
import com.ydzz.admin.dto.AdminLoginVo;
import com.ydzz.admin.dto.ChangePasswordRequest;
import com.ydzz.admin.log.OperationLog;
import com.ydzz.admin.service.AdminAuthService;
import com.ydzz.common.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 后台认证接口。
 *
 * @author WebsiteServer
 * @since 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/api/admin/auth")
@Tag(name = "后台-认证", description = "管理员登录/登出/信息/改密")
public class AdminAuthController {

    private final AdminAuthService authService;

    public AdminAuthController(AdminAuthService authService) {
        this.authService = authService;
    }

    @Operation(summary = "获取图形验证码")
    @GetMapping("/captcha")
    public Result<java.util.Map<String, String>> captcha() {
        return Result.success(authService.generateCaptcha());
    }

    @Operation(summary = "管理员登录")
    @PostMapping("/login")
    public Result<AdminLoginVo> login(@Valid @RequestBody AdminLoginRequest req, HttpServletRequest request) {
        return Result.success("登录成功", authService.login(req, request));
    }

    @Operation(summary = "登出")
    @PostMapping("/logout")
    public Result<String> logout(HttpServletRequest request) {
        authService.logout(request);
        return Result.success("登出成功", "ok");
    }

    @Operation(summary = "当前管理员信息+权限+菜单")
    @GetMapping("/info")
    public Result<AdminInfoVo> info() {
        return Result.success(authService.currentInfo());
    }

    @Operation(summary = "修改密码")
    @OperationLog(module = "认证", operation = "修改密码")
    @PostMapping("/change-password")
    public Result<String> changePassword(@Valid @RequestBody ChangePasswordRequest req) {
        authService.changePassword(req.getOldPassword(), req.getNewPassword());
        return Result.success("修改成功", "ok");
    }
}
