package com.ydzz.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ydzz.admin.entity.AdminLoginLog;
import org.apache.ibatis.annotations.Mapper;

/**
 * 后台登录日志 Mapper
 *
 * @author WebsiteServer
 * @since 1.0.0
 */
@Mapper
public interface AdminLoginLogMapper extends BaseMapper<AdminLoginLog> {
}
