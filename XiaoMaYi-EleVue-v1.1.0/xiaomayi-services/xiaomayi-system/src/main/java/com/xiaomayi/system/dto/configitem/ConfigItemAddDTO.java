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

package com.xiaomayi.system.dto.configitem;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

/**
 * <p>
 * 配置项
 * </p>
 *
 * @author 小蚂蚁云团队
 * @since 2024-03-23
 */
@Data
@Schema(name = "配置项添加DTO", description = "配置项添加DTO")
public class ConfigItemAddDTO {

    @Schema(description = "配置ID")
    @NotNull(message = "配置ID不能为空")
    @Min(value = 1, message = "配置ID必须大于0")
    private Integer configId;

    @Schema(description = "配置项名称")
    @NotBlank(message = "配置项名称不能为空")
    @Size(max = 100, message = "配置项名称最多100个字符")
    private String name;

    @Schema(description = "配置项编码")
    @NotBlank(message = "配置项编码不能为空")
    @Size(max = 100, message = "配置项编码最多100个字符")
    private String code;

    @Schema(description = "配置项值")
    private String value;

    @Schema(description = "配置项数据源")
    @Size(max = 1000, message = "配置项数据源最多1000个字符")
    private String options;

    @Schema(description = "配置项类型：hidden=隐藏 readonly=只读文本 number=数字 text=单行文本 textarea=多行文本	 password=密码 radio=单选框 checkbox=复选框 select=下拉框 icon=字体图标 date=日期 datetime=时间 image=单张图片 images=多张图片 file=单个文件 files=多个文件 ueditor=富文本编辑器")
    @NotBlank(message = "配置项类型不能为空")
    private String type;

    @Schema(description = "配置项状态：1-正常 2-停用")
    @NotNull(message = "配置项状态不能为空")
    @Range(min = 1, max = 2, message = "配置项状态值在1-2之间")
    private Integer status;

    @Schema(description = "配置项排序")
    @NotNull(message = "配置项排序不能为空")
    @Min(value = 0, message = "排序不得小于0")
    private Integer sort;

    @Schema(description = "配置项备注")
    private String note;

}
