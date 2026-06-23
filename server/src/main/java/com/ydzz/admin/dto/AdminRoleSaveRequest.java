package com.ydzz.admin.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 新增/编辑角色请求
 *
 * @author WebsiteServer
 * @since 1.0.0
 */
@Data
@Schema(description = "新增/编辑角色请求")
public class AdminRoleSaveRequest {

    private Long id;

    @NotBlank(message = "角色编码不能为空")
    private String roleCode;

    @NotBlank(message = "角色名称不能为空")
    private String roleName;

    private String description;

    @Schema(description = "状态：0禁用 1启用")
    private Integer status;

    private Integer sortOrder;
}
