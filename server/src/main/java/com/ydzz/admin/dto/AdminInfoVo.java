package com.ydzz.admin.dto;

import com.ydzz.admin.vo.MenuNode;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 当前管理员信息（含权限码、角色码、菜单树）
 *
 * @author WebsiteServer
 * @since 1.0.0
 */
@Data
@Schema(description = "当前管理员信息")
public class AdminInfoVo {

    private Long id;
    private String username;
    private String realName;
    private String avatar;

    @Schema(description = "角色编码列表")
    private List<String> roles;

    @Schema(description = "权限码列表")
    private List<String> permissions;

    @Schema(description = "菜单树")
    private List<MenuNode> menus;
}
