package com.ydzz.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ydzz.admin.entity.AdminRolePermission;
import org.apache.ibatis.annotations.Mapper;

/**
 * 角色-权限关联 Mapper
 *
 * @author WebsiteServer
 * @since 1.0.0
 */
@Mapper
public interface AdminRolePermissionMapper extends BaseMapper<AdminRolePermission> {
}
