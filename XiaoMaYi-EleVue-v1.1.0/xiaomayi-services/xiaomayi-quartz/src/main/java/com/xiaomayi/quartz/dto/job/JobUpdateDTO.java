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

package com.xiaomayi.quartz.dto.job;

import com.xiaomayi.core.annotation.Cron;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

/**
 * <p>
 * 定时任务调度
 * </p>
 *
 * @author 小蚂蚁云团队
 * @since 2024-06-13
 */
@Data
@Schema(name = "定时任务调度更新DTO", description = "定时任务调度更新DTO")
public class JobUpdateDTO {

    @Schema(description = "任务ID")
    @NotNull(message = "任务ID不能为空")
    @Min(value = 1, message = "任务ID必须大于0")
    private Integer id;

    @Schema(description = "任务名称")
    @NotBlank(message = "任务名称不能为空")
    @Size(max = 255, message = "任务名称最多255个字符")
    private String jobName;

    @Schema(description = "任务别名")
    @NotBlank(message = "任务别名不能为空")
    @Size(max = 255, message = "任务别名最多255个字符")
    private String jobAlias;

    @Schema(description = "任务分组")
    @NotBlank(message = "任务分组不能为空")
    @Size(max = 255, message = "任务分组最多255个字符")
    private String jobGroup;

    @Schema(description = "任务触发器")
    @NotBlank(message = "任务触发器不能为空")
    @Size(max = 255, message = "任务触发器最多255个字符")
    private String jobTrigger;

    @Schema(description = "任务状态：0-未发布 1-运行中 2-暂停")
    @NotNull(message = "任务状态不能为空")
    @Range(min = 0, max = 2, message = "任务状态值在0-2之间")
    private Integer status;

    @Schema(description = "正则表达式")
    @NotBlank(message = "正则表达式不能为空")
    @Cron(message = "cron表达式格式不正确")
    private String cronExpression;

    @Schema(description = "执行策略：1-立即执行 2-执行一次 3-放弃执行")
    @NotNull(message = "执行策略不能为空")
    @Range(max = 255, message = "执行策略最多255个字符")
    private Integer executePolicy;

    @Schema(description = "是否同步任务：0-否 1-是")
    @NotNull(message = "是否同步任务不能为空")
    @Range(min = 0, max = 1, message = "是否同步值在0-1之间")
    private Integer isSync;

    @Schema(description = "任务URL")
    @NotBlank(message = "任务URL不能为空")
    @Size(max = 255, message = "任务URL最多255个字符")
    private String url;

    @Schema(description = "任务备注")
    @NotBlank(message = "任务备注不能为空")
    @Size(max = 255, message = "任务备注最多255个字符")
    private String note;

}