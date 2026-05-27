package com.ydzz.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 用户实体类
 *
 * @author FortuneTelling
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("user")
@Schema(description = "用户实体")
public class User implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * 主键ID
	 */
	@TableId(value = "id", type = IdType.AUTO)
	@Schema(description = "主键ID", example = "1")
	private Long id;

	/**
	 * 手机号
	 */
	@TableField("phoneCode")
	@Schema(description = "手机号", example = "13800138000")
	private String phoneCode;

	/**
	 * 昵称
	 */
	@TableField("nickName")
	@Schema(description = "昵称", example = "小明")
	private String nickName;

	/**
	 * 性别: 1男 0女
	 */
	@TableField("gender")
	@Schema(description = "性别:1男 0女", example = "1")
	private Integer gender;

	/**
	 * 出生时间戳（毫秒）
	 */
	@TableField("birthTime")
	@Schema(description = "出生时间戳（毫秒）", example = "1704067200000")
	private Long birthTime;

	/**
	 * 出生区县
	 */
	@TableField("birthArea")
	@Schema(description = "出生区县", example = "海淀区")
	private String birthArea;

	/**
	 * 出生城市
	 */
	@TableField("birthCity")
	@Schema(description = "出生城市", example = "北京市")
	private String birthCity;

	/**
	 * 出生省份
	 */
	@TableField("birthProvince")
	@Schema(description = "出生省份", example = "北京市")
	private String birthProvince;

	/**
	 * 是否是vip，1是0否
	 */
	@TableField("isVip")
	@Schema(description = "是否是vip，1是0否", example = "0")
	private Integer isVip;

	/**
	 * 断事笔记
	 */
	@TableField("notes")
	@Schema(description = "断事笔记")
	private String notes;

	/**
	 * 标签组（JSON或逗号分隔均可）
	 */
	@TableField("tags")
	@Schema(description = "标签组", example = "[\"事业\",\"感情\"]")
	private String tags;

	/**
	 * 头像URL
	 */
	@TableField("avatar")
	@Schema(description = "头像URL")
	private String avatar;

	/**
	 * 状态：0-禁用，1-启用
	 */
	@TableField("status")
	@Schema(description = "状态：0-禁用，1-启用", example = "1")
	private Integer status;

	/**
	 * 创建时间
	 */
	@TableField(value = "createTime", fill = FieldFill.INSERT)
	@Schema(description = "创建时间", example = "2024-01-01T00:00:00")
	private LocalDateTime createTime;

	/**
	 * 更新时间
	 */
	@TableField(value = "updateTime", fill = FieldFill.INSERT_UPDATE)
	@Schema(description = "更新时间", example = "2024-01-01T00:00:00")
	private LocalDateTime updateTime;
} 