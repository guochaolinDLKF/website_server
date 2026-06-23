package com.ydzz.admin.business.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ydzz.admin.business.entity.Payment;
import org.apache.ibatis.annotations.Mapper;

/**
 * 支付（zhouyi.payments，只读）。
 *
 * @author WebsiteServer
 * @since 1.0.0
 */
@Mapper
@DS("zhouyi")
public interface PaymentMapper extends BaseMapper<Payment> {
}
