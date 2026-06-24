package com.ydzz.admin.business.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单（zhouyi.orders，只读）。
 * 字段依据《运营后台系统策划案》3.1，以真实 zhouyi 表为准。
 *
 * @author WebsiteServer
 * @since 1.0.0
 */
@Data
@TableName("orders")
@Schema(description = "订单")
public class Orders implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private String orderNo;
    private Long userId;
    private String itemId;
    private String itemName;
    private Integer itemType;
    private Integer vipMonth;
    private BigDecimal originalPrice;
    private BigDecimal discountPrice;
    private String orderStatus;
    private String tradeType;
    private String paySource;
    private String payType;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    /** 删除标志：0-未删除，1-已删除（orders 表使用 delete_flag，与全局 deleted 字段不同，需单独标注） */
    @TableLogic(value = "0", delval = "1")
    private Integer deleteFlag;
}
