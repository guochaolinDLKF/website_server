package com.ydzz.admin.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 菜单树节点（用于前端动态路由/侧边菜单渲染）
 *
 * @author WebsiteServer
 * @since 1.0.0
 */
@Data
@Schema(description = "菜单树节点")
public class MenuNode {

    private Long id;
    private Long parentId;
    private String permissionCode;
    private String name;
    private Integer type;
    private String path;
    private String component;
    private String icon;
    private Integer sortOrder;

    private List<MenuNode> children = new ArrayList<>();
}
