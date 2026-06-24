package com.ydzz.admin.business.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * IAP 商品（zhouyi.products，只读）。
 * 字段依据《运营后台系统策划案》3.1，以真实 zhouyi 表为准（仅 id/product_id/product_desc/item_type）。
 *
 * @author WebsiteServer
 * @since 1.0.0
 */
@Data
@TableName("products")
@Schema(description = "IAP 商品")
public class Product implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private String productId;
    private String productDesc;
    private Integer itemType;
}
