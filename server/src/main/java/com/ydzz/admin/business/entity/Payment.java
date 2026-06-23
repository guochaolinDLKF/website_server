package com.ydzz.admin.business.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 支付（zhouyi.payments，只读）。
 * 字段依据《运营后台系统策划案》3.1，以真实 zhouyi 表为准。
 *
 * @author WebsiteServer
 * @since 1.0.0
 */
@Data
@TableName("payments")
@Schema(description = "支付")
public class Payment implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private String paymentNo;
    private String orderNo;
    private Long userId;
    private BigDecimal paymentAmount;
    private String paymentMethod;
    private String paymentStatus;
    private String thirdPartyTradeNo;
    private LocalDateTime paymentTime;
    private LocalDateTime expireTime;
    private LocalDateTime callbackTime;
}
