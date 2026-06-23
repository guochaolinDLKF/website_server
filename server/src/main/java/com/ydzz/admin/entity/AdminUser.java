package com.ydzz.admin.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 后台管理员实体
 *
 * @author WebsiteServer
 * @since 1.0.0
 */
@Data
@TableName("admin_user")
@Schema(description = "后台管理员")
public class AdminUser implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @Schema(description = "登录账号")
    private String username;

    /** 密码：只写不读，避免随响应外泄 */
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    @Schema(description = "姓名")
    private String realName;

    private String avatar;

    private String phone;

    private String email;

    @Schema(description = "状态：0禁用 1启用")
    private Integer status;

    private LocalDateTime lastLoginTime;

    private String lastLoginIp;

    private String remark;

    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;
}
