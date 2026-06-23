package com.ydzz.admin.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 后台登录请求
 *
 * @author WebsiteServer
 * @since 1.0.0
 */
@Data
@Schema(description = "后台登录请求")
public class AdminLoginRequest {

    @NotBlank(message = "账号不能为空")
    @Schema(description = "登录账号", example = "admin")
    private String username;

    @NotBlank(message = "密码不能为空")
    @Schema(description = "密码", example = "Admin@123")
    private String password;
}
