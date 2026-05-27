package com.ydzz.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 登录响应DTO
 * 包含Token和用户信息（排除敏感字段）
 *
 * @author FortuneTelling
 * @since 1.0.0
 */
@Data
@Schema(description = "登录响应数据")
public class LoginResponse {

    @Schema(description = "访问令牌", example = "encrypted_token_string")
    private String token;

    @Schema(description = "用户信息")
    private UserInfo userInfo;

    @Data
    @Schema(description = "用户信息（登录返回）")
    public static class UserInfo {
        
        @Schema(description = "手机号", example = "13800138000")
        private String phoneCode;

        @Schema(description = "昵称", example = "小明")
        private String nickName;

        @Schema(description = "性别:1男 0女", example = "1")
        private Integer gender;

        @Schema(description = "出生时间戳（毫秒）", example = "1704067200000")
        private Long birthTime;

        @Schema(description = "出生区县", example = "海淀区")
        private String birthArea;

        @Schema(description = "出生城市", example = "北京市")
        private String birthCity;

        @Schema(description = "出生省份", example = "北京市")
        private String birthProvince;

        @Schema(description = "是否是vip，1是0否", example = "0")
        private Integer isVip;

        @Schema(description = "断事笔记")
        private String notes;

        @Schema(description = "头像URL")
        private String avatar;

        @Schema(description = "状态：0-禁用，1-启用", example = "1")
        private Integer status;
    }
}
