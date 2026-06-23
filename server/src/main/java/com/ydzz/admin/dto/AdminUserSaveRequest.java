package com.ydzz.admin.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

/**
 * 新增/编辑管理员请求
 *
 * @author WebsiteServer
 * @since 1.0.0
 */
@Data
@Schema(description = "新增/编辑管理员请求")
public class AdminUserSaveRequest {

    @Schema(description = "ID（编辑时必填）")
    private Long id;

    @NotBlank(message = "账号不能为空")
    private String username;

    @Schema(description = "密码（新增必填；编辑留空表示不修改）")
    private String password;

    private String realName;

    private String phone;

    private String email;

    @Schema(description = "状态：0禁用 1启用")
    private Integer status;

    private String remark;

    @Schema(description = "角色ID列表")
    private List<Long> roleIds;
}
