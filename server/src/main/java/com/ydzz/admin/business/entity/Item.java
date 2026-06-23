package com.ydzz.admin.business.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 商品/付费功能（zhouyi.items，只读 + 受控编辑价格/描述）。
 * 字段依据《运营后台系统策划案》3.1/3.3，以真实 zhouyi 表为准。
 * 注：策划案提及的 status/sort_order/func_switch/usage_limit 为「需 ALTER 追加」字段，
 *     真实表未追加前不在此映射，避免读写不存在的列。
 *
 * @author WebsiteServer
 * @since 1.0.0
 */
@Data
@TableName("items")
@Schema(description = "商品/付费功能")
public class Item implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private String itemId;
    private String itemDesc;
    private Integer itemType;
    private BigDecimal itemPrice;
    private BigDecimal itemDiscount;
}
