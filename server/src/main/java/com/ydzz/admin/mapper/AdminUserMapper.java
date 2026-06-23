package com.ydzz.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ydzz.admin.entity.AdminUser;
import org.apache.ibatis.annotations.Mapper;

/**
 * 后台管理员 Mapper
 *
 * @author WebsiteServer
 * @since 1.0.0
 */
@Mapper
public interface AdminUserMapper extends BaseMapper<AdminUser> {
}
