package com.ydzz.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ydzz.admin.entity.AdminUserRole;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 管理员-角色关联 Mapper
 *
 * @author WebsiteServer
 * @since 1.0.0
 */
@Mapper
public interface AdminUserRoleMapper extends BaseMapper<AdminUserRole> {

    /**
     * 查询某管理员的角色编码
     */
    @Select("""
            SELECT r.role_code
            FROM admin_role r
            JOIN admin_user_role ur ON ur.role_id = r.id
            WHERE ur.admin_id = #{adminId} AND r.status = 1 AND r.deleted = 0
            """)
    List<String> selectRoleCodesByAdminId(Long adminId);
}
