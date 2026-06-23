package com.ydzz.admin.business.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ydzz.admin.business.entity.UserBenefit;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户权益（zhouyi.user_benefits，只读）。
 *
 * @author WebsiteServer
 * @since 1.0.0
 */
@Mapper
@DS("zhouyi")
public interface UserBenefitMapper extends BaseMapper<UserBenefit> {
}
