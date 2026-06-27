package com.ydzz.admin.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 后台登录日志
 *
 * @author WebsiteServer
 * @since 1.0.0
 */
@Data
@TableName("admin_login_log")
@Schema(description = "后台登录日志")
public class AdminLoginLog implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private Long adminId;

    private String username;

    private String loginIp;

    @Schema(description = "登录地区(高德IP定位)")
    private String loginRegion;

    @Schema(description = "是否异常登录(国外IP):1是0否")
    private Integer abnormal;

    private String userAgent;

    @Schema(description = "1登录 2登出")
    private Integer loginType;

    @Schema(description = "1成功 0失败")
    private Integer status;

    private String msg;

    private LocalDateTime loginTime;
}
