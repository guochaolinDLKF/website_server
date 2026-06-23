package com.ydzz.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ydzz.admin.entity.AdminOperationLog;
import org.apache.ibatis.annotations.Mapper;

/**
 * 后台操作日志 Mapper
 *
 * @author WebsiteServer
 * @since 1.0.0
 */
@Mapper
public interface AdminOperationLogMapper extends BaseMapper<AdminOperationLog> {
}
