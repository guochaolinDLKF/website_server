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

package com.xiaomayi.web.dto.link;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * <p>
 * 友链
 * </p>
 *
 * @author 小蚂蚁云团队
 * @since 2024-07-01
 */
@Data
@Schema(name = "友链添加DTO", description = "友链添加DTO")
public class LinkAddDTO {

    @Schema(description = "租户ID")
    @NotNull(message = "租户ID不能为空")
    private Integer tenantId;

    @Schema(description = "友链名称")
    @NotBlank(message = "友链名称不能为空")
    private String name;

    @Schema(description = "友链类型：1-友情链接 2-合作伙伴")
    @NotNull(message = "友链类型不能为空")
    private Integer type;

    @Schema(description = "友链地址")
    @NotBlank(message = "友链地址不能为空")
    private String url;

    @Schema(description = "友链形式：1-文字链接 2-图片链接")
    @NotNull(message = "友链形式不能为空")
    private Integer form;

    @Schema(description = "友链图片")
    @NotBlank(message = "友链图片不能为空")
    private String image;

    @Schema(description = "友链状态：1-在用 2-停用")
    @NotNull(message = "友链状态不能为空")
    private Integer status;

    @Schema(description = "友链排序")
    @NotNull(message = "友链排序不能为空")
    private Integer sort;

}