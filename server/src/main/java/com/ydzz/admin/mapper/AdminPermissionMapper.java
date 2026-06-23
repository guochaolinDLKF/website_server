package com.ydzz.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ydzz.admin.entity.AdminPermission;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 后台权限/菜单 Mapper
 *
 * @author WebsiteServer
 * @since 1.0.0
 */
@Mapper
public interface AdminPermissionMapper extends BaseMapper<AdminPermission> {

    /**
     * 查询某管理员（经由其角色）拥有的全部权限码
     */
    @Select("""
            SELECT DISTINCT p.permission_code
            FROM admin_permission p
            JOIN admin_role_permission rp ON rp.permission_id = p.id
            JOIN admin_user_role ur ON ur.role_id = rp.role_id
            JOIN admin_role r ON r.id = ur.role_id AND r.status = 1 AND r.deleted = 0
            WHERE ur.admin_id = #{adminId}
              AND p.status = 1 AND p.deleted = 0
              AND p.permission_code IS NOT NULL AND p.permission_code <> ''
            """)
    List<String> selectPermissionCodesByAdminId(Long adminId);

    /**
     * 查询某管理员拥有的权限实体（用于构建菜单树）
     */
    @Select("""
            SELECT DISTINCT p.*
            FROM admin_permission p
            JOIN admin_role_permission rp ON rp.permission_id = p.id
            JOIN admin_user_role ur ON ur.role_id = rp.role_id
            JOIN admin_role r ON r.id = ur.role_id AND r.status = 1 AND r.deleted = 0
            WHERE ur.admin_id = #{adminId}
              AND p.status = 1 AND p.deleted = 0
            ORDER BY p.sort_order ASC
            """)
    List<AdminPermission> selectPermissionsByAdminId(Long adminId);
}
