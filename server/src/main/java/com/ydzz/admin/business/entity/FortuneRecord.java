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
 * 运势记录（zhouyi.fortune_record，只读）。列为驼峰命名，需显式 @TableField。
 * 字段依据《运营后台系统策划案》3.1，以真实 zhouyi 表为准。
 *
 * @author WebsiteServer
 * @since 1.0.0
 */
@Data
@TableName("fortune_record")
@Schema(description = "运势记录")
public class FortuneRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("userId")
    private Long userId;
    @TableField("eightCharId")
    private Long eightCharId;
    @TableField("fortuneType")
    private Integer fortuneType;
    @TableField("fortuneDate")
    private String fortuneDate;

    private String content;

    @TableField("fortuneLevel")
    private Integer fortuneLevel;
    @TableField("isTop")
    private Integer isTop;
    @TableField("createTime")
    private LocalDateTime createTime;
}
