// +----------------------------------------------------------------------
// | 小蚂蚁云企业级开发框架 [ 赋能开发者，助力企业发展 ]
// +----------------------------------------------------------------------
// | 版权所有 2020~2025 小蚂蚁云团队
// +----------------------------------------------------------------------
// | Licensed Apache-2.0 【小蚂蚁云】并不是自由软件，未经许可禁止去掉相关版权
// +----------------------------------------------------------------------
// | 官方网站: https://www.xiaomayicloud.com
// +----------------------------------------------------------------------
// | 软件作者: @小蚂蚁云团队 团队荣誉出品
// +----------------------------------------------------------------------
// | 版权和免责声明:
// | 本团队对该软件框架产品拥有知识产权（包括但不限于商标权、专利权、著作权、商业秘密等）
// | 均受到相关法律法规的保护，任何个人、组织和单位不得在未经本团队书面授权的情况下对所授权
// | 软件框架产品本身申请相关的知识产权，被授权主体务必妥善保管官方所授权的软件产品源码，禁
// | 止以任何形式对外泄露(包括但不限于分享、开源、网络平台),禁止用于任何违法、侵害他人合法
// | 权益等恶意的行为，禁止用于任何违反我国法律法规的一切项目研发，任何个人、组织和单位用于
// | 项目研发而产生的任何意外、疏忽、合约毁坏、诽谤、版权或知识产权侵犯及其造成的损失 (包括
// | 但不限于直接、间接、附带或衍生的损失等)，本团队不承担任何法律责任，本软件框架禁止任何
// | 单位、组织、个人用于任何违法、侵害他人合法利益等恶意的行为，如有发现违规、违法的犯罪行
// | 为，本团队将无条件配合公安机关调查取证同时保留一切以法律手段起诉的权利，本软件框架只能
// | 用于公司和个人内部的法律所允许的合法合规的软件产品研发，详细声明内容请阅读《框架免责声
// | 明》附件；
// +----------------------------------------------------------------------

package com.xiaomayi.system.dto.tenant;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.hibernate.validator.constraints.Range;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

/**
 * <p>
 * 租户
 * </p>
 *
 * @author 小蚂蚁云团队
 * @since 2024-07-05
 */
@Data
@Schema(name = "租户更新DTO", description = "租户更新DTO")
public class TenantUpdateDTO {

    @Schema(description = "租户ID")
    private Integer id;

    @Schema(description = "租户编号")
    @NotBlank(message = "租户编号不能为空")
    @Size(max = 100, message = "租户编号最多100个字符")
    private String code;

    @Schema(description = "租户名称")
    @NotBlank(message = "租户名称不能为空")
    @Size(max = 150, message = "租户名称最多150个字符")
    private String name;

    @Schema(description = "租户图片")
    @NotBlank(message = "租户图片不能为空")
    @Size(max = 255, message = "租户图片最多255个字符")
    private String image;

    @Schema(description = "统一社会信用代码")
    @NotBlank(message = "统一社会信用代码不能为空")
    @Size(max = 100, message = "统一社会信用代码最多100个字符")
    private String license;

    @Schema(description = "联系人")
    @NotBlank(message = "联系人不能为空")
    @Size(max = 100, message = "联系人最多100个字符")
    private String contactUser;

    @Schema(description = "联系电话")
    @NotBlank(message = "联系电话不能为空")
    @Size(max = 100, message = "联系电话最多100个字符")
    private String contactMobile;

    @Schema(description = "电子邮件")
    @NotBlank(message = "电子邮件不能为空")
    @Size(max = 100, message = "电子邮件最多100个字符")
    private String contactEmail;

    @Schema(description = "租户地址")
    @NotBlank(message = "租户地址不能为空")
    @Size(max = 255, message = "租户地址最多255个字符")
    private String contactAddress;

    @Schema(description = "租户简介")
    @NotBlank(message = "租户简介不能为空")
    @Size(max = 255, message = "租户简介最多255个字符")
    private String contactIntro;

    @Schema(description = "租户官网")
    @NotBlank(message = "租户官网不能为空")
    @Size(max = 255, message = "租户官网最多255个字符")
    private String contactSite;

    @Schema(description = "租户备注")
    @Size(max = 255, message = "租户备注最多255个字符")
    private String contactNote;

    @Schema(description = "过期时间")
    @NotNull(message = "过期时间不能为空")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime expireTime;

    @Schema(description = "租户限额（-1不限制）")
    @NotNull(message = "租户限额（-1不限制）不能为空")
    private Integer number;

    @Schema(description = "租户状态：0-正常 1-禁用")
    @NotNull(message = "租户状态不能为空")
    @Range(min = 0, max = 1, message = "租户状态值在0-1之间")
    private Integer status;

}