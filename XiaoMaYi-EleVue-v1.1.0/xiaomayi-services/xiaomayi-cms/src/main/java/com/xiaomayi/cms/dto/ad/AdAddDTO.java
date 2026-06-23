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

package com.xiaomayi.cms.dto.ad;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.hibernate.validator.constraints.Range;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

/**
 * <p>
 * 广告
 * </p>
 *
 * @author 小蚂蚁云团队
 * @since 2024-07-03
 */
@Data
@Schema(name = "广告添加DTO", description = "广告添加DTO")
public class AdAddDTO {

    @Schema(description = "广告标题")
    @NotBlank(message = "广告标题不能为空")
    @Size(max = 100, message = "广告标题最多100个字符")
    private String title;

    @Schema(description = "广告位ID")
    @NotNull(message = "广告位ID不能为空")
    @Min(value = 1, message = "广告位ID必须大于0")
    private Integer adSortId;

    @Schema(description = "广告图片")
    @NotBlank(message = "广告图片不能为空")
    @Size(max = 255, message = "广告图片最多255个字符")
    private String cover;

    @Schema(description = "广告格式：1-图片 2-文字 3-视频 4-推荐")
    @NotNull(message = "广告格式不能为空")
    @Range(min = 1, max = 4, message = "广告格式值在1-4之间")
    private Integer type;

    @Schema(description = "广告描述")
    @NotBlank(message = "广告描述不能为空")
    @Size(max = 255, message = "广告描述最多255个字符")
    private String note;

    @Schema(description = "广告内容")
    private String content;

    @Schema(description = "广告链接")
    @NotBlank(message = "广告链接不能为空")
    private String url;

    @Schema(description = "广告宽度")
    @NotNull(message = "广告宽度不能为空")
    @Min(value = 1, message = "广告宽度必须大于0")
    private Integer width;

    @Schema(description = "广告高度")
    @NotNull(message = "广告高度不能为空")
    @Min(value = 1, message = "广告高度必须大于0")
    private Integer height;

    @Schema(description = "开始时间")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @NotNull(message = "开始时间不能为空")
    private LocalDateTime startTime;

    @Schema(description = "结束时间")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @NotNull(message = "结束时间不能为空")
    private LocalDateTime endTime;

    @Schema(description = "广告点击率")
    @NotNull(message = "广告点击率不能为空")
    @Min(value = 0, message = "广告高度不得小于0")
    private Integer click;

    @Schema(description = "广告状态：1-在用 2-停用")
    @NotNull(message = "广告状态不能为空")
    @Range(min = 1, max = 2, message = "广告状态值在1-2之间")
    private Integer status;

    @Schema(description = "广告排序")
    @NotNull(message = "广告排序不能为空")
    @Min(value = 0, message = "广告排序不得小于0")
    private Integer sort;

}