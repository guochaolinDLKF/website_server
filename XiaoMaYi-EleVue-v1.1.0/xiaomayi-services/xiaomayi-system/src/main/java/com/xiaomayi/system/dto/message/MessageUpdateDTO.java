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

package com.xiaomayi.system.dto.message;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

/**
 * <p>
 * 消息
 * </p>
 *
 * @author 小蚂蚁云团队
 * @since 2024-08-06
 */
@Data
@Schema(name = "消息更新DTO", description = "消息更新DTO")
public class MessageUpdateDTO {

    @Schema(description = "消息ID")
    private Integer id;

    @Schema(description = "消息编号")
    @NotBlank(message = "消息编号不能为空")
    private String number;

    @Schema(description = "消息标题")
    @NotBlank(message = "消息标题不能为空")
    private String title;

    @Schema(description = "消息类型：1-系统通知 2-用户私信 3-代办事项")
    @NotNull(message = "消息类型：1-系统通知 2-用户私信 3-代办事项不能为空")
    private Integer type;

    @Schema(description = "接收人ID")
    @NotNull(message = "接收人ID不能为空")
    private Integer userId;

    @Schema(description = "用户微信OPENID")
    @NotBlank(message = "用户微信OPENID不能为空")
    private String openId;

    @Schema(description = "手机号")
    @NotBlank(message = "手机号不能为空")
    private String mobile;

    @Schema(description = "消息参数")
    @NotBlank(message = "消息参数不能为空")
    private String param;

    @Schema(description = "消息内容")
    @NotBlank(message = "消息内容不能为空")
    private String content;

    @Schema(description = "业务类型：1-订单 2-其他")
    @NotNull(message = "业务类型：1-订单 2-其他不能为空")
    private Integer bizType;

    @Schema(description = "业务ID")
    @NotNull(message = "业务ID不能为空")
    private Integer bizId;

    @Schema(description = "业务内容")
    @NotBlank(message = "业务内容不能为空")
    private String bizContent;

    @Schema(description = "消息状态：0-未读 1-已读")
    @NotNull(message = "消息状态：0-未读 1-已读不能为空")
    private Integer status;

    @Schema(description = "发送时间")
    private LocalDateTime sendTime;

    @Schema(description = "读取时间")
    private LocalDateTime readTime;

}