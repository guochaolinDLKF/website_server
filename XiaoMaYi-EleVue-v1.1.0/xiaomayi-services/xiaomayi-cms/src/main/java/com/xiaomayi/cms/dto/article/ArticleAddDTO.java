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

package com.xiaomayi.cms.dto.article;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

/**
 * <p>
 * 文章
 * </p>
 *
 * @author 小蚂蚁云团队
 * @since 2024-06-24
 */
@Data
@Schema(name = "文章添加DTO", description = "文章添加DTO")
public class ArticleAddDTO {

    @Schema(description = "分类ID")
    @NotNull(message = "分类ID不能为空")
    @Min(value = 1, message = "分类ID必须大于0")
    private Integer categoryId;

    @Schema(description = "文章标题")
    @NotBlank(message = "文章标题不能为空")
    @Size(max = 150, message = "文章标题最多150个字符")
    private String title;

    @Schema(description = "文章封面")
    @NotBlank(message = "文章封面不能为空")
    @Size(max = 255, message = "文章封面最多255个字符")
    private String cover;

    @Schema(description = "文章图集")
    @NotEmpty(message = "文章图集不能为空")
    private String images;

    @Schema(description = "文章导读")
    @NotBlank(message = "文章导读不能为空")
    @Size(max = 255, message = "文章导读最多255个字符")
    private String intro;

    @Schema(description = "文章内容")
    @NotBlank(message = "文章内容不能为空")
    private String content;

    @Schema(description = "文章作者")
    @NotBlank(message = "文章作者不能为空")
    @Size(max = 100, message = "文章作者最多100个字符")
    private String author;

    @Schema(description = "文章状态：0-正常 1-下架")
    @NotNull(message = "文章状态不能为空")
    @Range(min = 0, max = 1, message = "文章状态值在0-1之间")
    private Integer status;

    @Schema(description = "文章点击率")
    @NotNull(message = "文章点击率不能为空")
    @Min(value = 1, message = "文章点击率必须大于0")
    private Integer click;

    @Schema(description = "文章排序")
    @NotNull(message = "文章排序不能为空")
    @Min(value = 0, message = "文章排序不得小于0")
    private Integer sort;

}