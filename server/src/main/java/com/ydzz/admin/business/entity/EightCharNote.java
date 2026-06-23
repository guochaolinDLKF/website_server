package com.ydzz.admin.business.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 断事笔记（zhouyi.eight_char_note，只读）。列为驼峰命名，需显式 @TableField。
 * 字段依据《运营后台系统策划案》3.1，以真实 zhouyi 表为准。
 *
 * @author WebsiteServer
 * @since 1.0.0
 */
@Data
@TableName("eight_char_note")
@Schema(description = "断事笔记")
public class EightCharNote implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("userId")
    private Long userId;
    @TableField("eightCharId")
    private Long eightCharId;
    @TableField("recordTime")
    private LocalDateTime recordTime;
    @TableField("timeType")
    private Integer timeType;
    @TableField("resultType")
    private Integer resultType;
    @TableField("eventType")
    private Integer eventType;

    private String content;

    @TableField("createTime")
    private LocalDateTime createTime;
    @TableField("isTop")
    private Integer isTop;
}
