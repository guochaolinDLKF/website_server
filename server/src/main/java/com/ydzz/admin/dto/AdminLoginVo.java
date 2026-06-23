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

    @Schema(description = "管理员信息")
    private AdminInfoVo adminInfo;
}
