package com.ydzz.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

/**
 * 登录请求DTO（手机号+验证码）
 *
 * @author FortuneTelling
 * @since 1.0.0
 */
@Data
@Schema(description = "登录请求参数（手机号+验证码）")
public class LoginRequest {

	@Schema(description = "手机号", example = "15801301435", required = true)
	@NotBlank(message = "手机号不能为空")
	@Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
	private String phoneCode;

	@Schema(description = "短信验证码", example = "888888", required = true)
	@NotBlank(message = "验证码不能为空")
	private String verifyCode;
} 