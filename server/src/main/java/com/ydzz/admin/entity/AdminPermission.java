package com.ydzz.admin.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 后台权限/菜单实体
 *
 * @author WebsiteServer
 * @since 1.0.0
 */
@Data
@TableName("admin_permission")
@Schema(description = "后台权限/菜单")
public class AdminPermission implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @Schema(description = "权限码，如 user:list")
    private String permissionCode;

    @Schema(description = "名称")
    private String permissionName;

    @Schema(description = "类型：1菜单 2按钮 3接口")
    private Integer permissionType;

    private Long parentId;

    @Schema(description = "前端路由/接口路径")
    private String path;

    private String component;

    private String icon;

    private Integer sortOrder;

    private Integer status;

    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;
}
