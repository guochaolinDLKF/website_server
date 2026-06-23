package com.ydzz.admin.business.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ydzz.entity.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户（zhouyi.user，只读）。复用 C 端真实实体 {@link User}，仅查询路由到 zhouyi。
 *
 * @author WebsiteServer
 * @since 1.0.0
 */
@Mapper
@DS("zhouyi")
public interface ZhouyiUserMapper extends BaseMapper<User> {
}
