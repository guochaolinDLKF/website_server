package com.ydzz.admin.service;

import com.ydzz.admin.entity.AdminPermission;
import com.ydzz.admin.mapper.AdminPermissionMapper;
import com.ydzz.admin.mapper.AdminUserRoleMapper;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 后台 RBAC 查询服务：供 Sa-Token 鉴权与「当前管理员信息」使用。
 *
 * @author WebsiteServer
 * @since 1.0.0
 */
@Service
public class AdminRbacService {

    private final AdminPermissionMapper permissionMapper;
    private final AdminUserRoleMapper userRoleMapper;

    public AdminRbacService(AdminPermissionMapper permissionMapper, AdminUserRoleMapper userRoleMapper) {
        this.permissionMapper = permissionMapper;
        this.userRoleMapper = userRoleMapper;
    }

    /** 管理员拥有的全部权限码（菜单/按钮/接口） */
    public List<String> listPermissionCodes(Long adminId) {
        return permissionMapper.selectPermissionCodesByAdminId(adminId);
    }

    /** 管理员拥有的角色编码 */
    public List<String> listRoleCodes(Long adminId) {
        return userRoleMapper.selectRoleCodesByAdminId(adminId);
    }

    /** 管理员可见的权限实体（用于构建前端菜单树） */
    public List<AdminPermission> listPermissions(Long adminId) {
        return permissionMapper.selectPermissionsByAdminId(adminId);
    }
}
