package com.ydzz.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 账户更新请求DTO
 * 
 * @author FortuneTelling
 * @since 1.0.0
 */
@Data
@Schema(description = "账户更新请求")
public class AccountUpdateRequest {

    @Schema(description = "用户名", example = "张三")
    private String name;

    @NotNull(message = "性别不能为空")
    @Schema(description = "性别: true男, false女", example = "true", required = true)
    private Boolean gender;

    @Schema(description = "出生省份(海外代指国家)", example = "海南省")
    private String birthProvince;

    @Schema(description = "出生城市(海外代指城市)", example = "海口")
    private String birthCity;

    @Schema(description = "出生区县", example = "龙华区")
    private String birthArea;

    @Schema(description = "出生时间戳", example = "1749804120")
    private Long birthTime;

    @Schema(description = "是否是vip: true or false", example = "false")
    private Boolean isVip;
} 