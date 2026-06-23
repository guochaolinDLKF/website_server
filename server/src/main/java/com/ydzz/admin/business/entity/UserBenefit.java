package com.ydzz.admin.business.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 用户权益（zhouyi.user_benefits，只读）。
 * 字段依据《运营后台系统策划案》3.1，以真实 zhouyi 表为准。
 *
 * @author WebsiteServer
 * @since 1.0.0
 */
@Data
@TableName("user_benefits")
@Schema(description = "用户权益")
public class UserBenefit implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private Long userId;
    private String itemId;
    private String itemName;
    private Integer itemType;
    private Integer vipMonth;
    private LocalDateTime purchaseTime;
    private LocalDateTime expireTime;
    private String orderNo;
    private Integer isValid;
}
