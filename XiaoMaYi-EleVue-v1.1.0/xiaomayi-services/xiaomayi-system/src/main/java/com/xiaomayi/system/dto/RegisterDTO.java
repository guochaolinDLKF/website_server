package com.xiaomayi.system.dto;

import com.xiaomayi.xss.annotation.Xss;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * <p>
 * 用户注册DTO
 * </p>
 *
 * @author 小蚂蚁云团队
 * @since 2024-05-21
 */
@Data
public class RegisterDTO {

    /**
     * 登录账号
     */
    @Schema(description = "登录账号")
    @Xss(message = "登录账号不能包含脚本字符")
    @NotBlank(message = "登录账号不能为空")
    @Size(max = 50, message = "登录账号最多50个字符")
    private String username;

    /**
     * 登录密码
     */
    @Schema(description = "登录密码")
    @Xss(message = "登录账号不能包含脚本字符")
    @NotBlank(message = "登录密码不能为空")
    @Size(min = 6, max = 12, message = "登录密码为6-12个字符")
    private String password;

    /**
     * 确认密码
     */
    @Schema(description = "登录密码")
    @Xss(message = "登录账号不能包含脚本字符")
    @NotBlank(message = "登录密码不能为空")
    @Size(min = 6, max = 12, message = "登录密码为6-12个字符")
    private String retPassword;

    /**
     * 手机号码
     */
    @Schema(description = "手机号码")
    @NotBlank(message = "手机号码不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String mobile;

    /**
     * 验证码
     */
    @Schema(description = "验证码")
    @NotBlank(message = "验证码不能为空")
    @Size(min = 3, max = 6, message = "验证码为3-6个字符")
    private String code;

    /**
     * 勾选注册协议
     */
    private Boolean agreement;

}
