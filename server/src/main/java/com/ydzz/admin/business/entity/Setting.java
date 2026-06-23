package com.ydzz.admin.business.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 用户设置（zhouyi.setting，只读）。列为驼峰命名，需显式 @TableField。
 * 字段依据《运营后台系统策划案》3.1，以真实 zhouyi 表为准。
 *
 * @author WebsiteServer
 * @since 1.0.0
 */
@Data
@TableName("setting")
@Schema(description = "用户设置")
public class Setting implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "userId")
    private Long userId;

    @TableField("isOpenMonth")
    private Integer isOpenMonth;
    @TableField("isOpenDay")
    private Integer isOpenDay;
    @TableField("isOpenHour")
    private Integer isOpenHour;
}
