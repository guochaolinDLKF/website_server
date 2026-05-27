package com.ydzz.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 账户更新响应DTO
 * 
 * @author FortuneTelling
 * @since 1.0.0
 */
@Data
@Schema(description = "账户更新响应")
public class AccountUpdateResponse {

    @Schema(description = "用户ID", example = "1")
    private Long id;

    @Schema(description = "手机号", example = "19919961991")
    private String phone;

    @Schema(description = "姓名", example = "张三")
    private String nickName;

    @Schema(description = "性别: true男, false女", example = "true")
    private Boolean gender;

    @Schema(description = "出生时间戳", example = "1749804120")
    private Long birthTime;

    @Schema(description = "出生区县", example = "龙华区")
    private String birthArea;

    @Schema(description = "出生城市(海外指代城市)", example = "海口")
    private String birthCity;

    @Schema(description = "出生省份(海外指代国家)", example = "海南省")
    private String birthProvince;

    @Schema(description = "是否是vip: true or false", example = "false")
    private Boolean isVip;

    @Schema(description = "备注")
    private String notes;

    @Schema(description = "头像")
    private String avatar;

    @Schema(description = "状态", example = "1")
    private Integer status;

    @Schema(description = "创建时间", example = "1749804120")
    private Long createTime;

    @Schema(description = "更新时间", example = "1749804120")
    private Long updateTime;
}
