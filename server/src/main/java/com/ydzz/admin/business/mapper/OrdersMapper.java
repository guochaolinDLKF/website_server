package com.ydzz.admin.business.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ydzz.admin.business.entity.Orders;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单（zhouyi.orders，只读）。
 *
 * @author WebsiteServer
 * @since 1.0.0
 */
@Mapper
@DS("zhouyi")
public interface OrdersMapper extends BaseMapper<Orders> {
}
