package com.ydzz.admin.business.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 用户八字记录（zhouyi.eight_record，只读）。列为驼峰命名，需显式 @TableField。
 * 字段依据《运营后台系统策划案》3.1，以真实 zhouyi 表为准。
 *
 * @author WebsiteServer
 * @since 1.0.0
 */
@Data
@TableName("eight_record")
@Schema(description = "用户八字记录")
public class EightRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("userId")
    private Long userId;

    private String name;
    private Integer gender;

    @TableField("birthProvince")
    private String birthProvince;
    @TableField("birthCity")
    private String birthCity;
    @TableField("birthArea")
    private String birthArea;
    @TableField("eightChar")
    private String eightChar;

    private String zodiac;

    /** 断事笔记（总笔记，eight_record.note） */
    @TableField("note")
    private String note;

    @TableField("birthTrueSolarTime")
    private Long birthTrueSolarTime;
    @TableField("birthSolarTime")
    private Long birthSolarTime;

    private String tag;

    @TableField("isTop")
    private Integer isTop;
    @TableField("useTime")
    private Long useTime;
}
