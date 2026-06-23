package com.ydzz.admin.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ydzz.admin.entity.AdminPermission;
import com.ydzz.admin.mapper.AdminPermissionMapper;
import com.ydzz.admin.vo.MenuNode;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 权限/菜单服务：树构建、维护。
 *
 * @author WebsiteServer
 * @since 1.0.0
 */
@Service
public class AdminPermissionService {

    private final AdminPermissionMapper permissionMapper;

    public AdminPermissionService(AdminPermissionMapper permissionMapper) {
        this.permissionMapper = permissionMapper;
    }

    /** 全部权限（按排序），用于权限管理页 */
    public List<AdminPermission> listAll() {
        LambdaQueryWrapper<AdminPermission> qw = new LambdaQueryWrapper<>();
        qw.orderByAsc(AdminPermission::getSortOrder);
        return permissionMapper.selectList(qw);
    }

    /** 全量权限菜单树 */
    public List<MenuNode> fullTree() {
        return buildTree(listAll());
    }

    /** 由权限实体列表构建菜单树（仅含菜单与按钮，前端按需使用） */
    public List<MenuNode> buildTree(List<AdminPermission> permissions) {
        Map<Long, MenuNode> nodeMap = new LinkedHashMap<>();
        for (AdminPermission p : permissions) {
            nodeMap.put(p.getId(), toNode(p));
        }
        List<MenuNode> roots = new ArrayList<>();
        for (MenuNode node : nodeMap.values()) {
            Long pid = node.getParentId();
            if (pid != null && pid != 0 && nodeMap.containsKey(pid)) {
                nodeMap.get(pid).getChildren().add(node);
            } else {
                roots.add(node);
            }
        }
        return roots;
    }

    private MenuNode toNode(AdminPermission p) {
        MenuNode node = new MenuNode();
        node.setId(p.getId());
        node.setParentId(p.getParentId());
        node.setPermissionCode(p.getPermissionCode());
        node.setName(p.getPermissionName());
        node.setType(p.getPermissionType());
        node.setPath(p.getPath());
        node.setComponent(p.getComponent());
        node.setIcon(p.getIcon());
        node.setSortOrder(p.getSortOrder());
        return node;
    }
}
