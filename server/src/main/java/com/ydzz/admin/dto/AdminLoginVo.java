package com.ydzz.admin.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 后台登录返回
 *
 * @author WebsiteServer
 * @since 1.0.0
 */
@Data
@Schema(description = "后台登录返回")
public class AdminLoginVo {

    @Schema(description = "Token 名称（请求头名）", example = "Authorization")
    private String tokenName;

    @Schema(description = "Token 值")
    private String tokenValue;

    @Schema(description = "Token 前缀（若配置 sa-token.token-prefix，则请求头需以「前缀 空格 Token」提交）", example = "satoken:")
    private String tokenPrefix;

    @Schema(description = "管理员信息")
    private AdminInfoVo adminInfo;
}
