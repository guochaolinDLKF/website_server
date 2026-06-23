package com.ydzz.admin.business.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ydzz.admin.business.entity.PaymentCallbackFailure;
import org.apache.ibatis.annotations.Mapper;

/**
 * 支付回调失败（zhouyi.payment_callback_failures，只读）。
 *
 * @author WebsiteServer
 * @since 1.0.0
 */
@Mapper
@DS("zhouyi")
public interface PaymentCallbackFailureMapper extends BaseMapper<PaymentCallbackFailure> {
}
