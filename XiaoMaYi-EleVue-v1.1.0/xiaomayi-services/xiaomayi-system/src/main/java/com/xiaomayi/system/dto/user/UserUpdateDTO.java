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

package com.xiaomayi.system.dto.user;

import com.xiaomayi.xss.annotation.Xss;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;
import org.hibernate.validator.constraints.Range;


/**
 * <p>
 * 用户
 * </p>
 *
 * @author 小蚂蚁云团队
 * @since 2024-03-23
 */
@Data
@Schema(name = "用户更新DTO", description = "用户更新DTO")
public class UserUpdateDTO {

    @Schema(description = "用户ID")
    @NotNull(message = "用户ID不能为空")
    @Min(value = 1, message = "用于ID必须大于0")
    private Integer id;

    @Schema(description = "用户名称")
    @NotBlank(message = "用户名称不能为空")
    @Size(max = 150, message = "用户名称最多150个字符")
    private String realname;

    @Schema(description = "用户性别：1-男 2-女 3-保密")
    @NotNull(message = "用户性别不能为空")
    @Range(min = 1, max = 3, message = "用户性别值在1-3之间")
    private Integer gender;

    @Schema(description = "用户头像")
    @NotBlank(message = "用户头像不能为空")
    @Size(max = 255, message = "用户头像最多255个字符")
    private String avatar;

    @Schema(description = "手机号码")
    @NotBlank(message = "手机号码不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String mobile;

    @Schema(description = "邮箱地址")
    @NotBlank(message = "邮箱地址不能为空")
    @Email(message = "邮箱格式不正确")
    private String email;

    @Schema(description = "部门ID")
    @NotNull(message = "部门ID不能为空")
    @Min(value = 1, message = "部门ID必须大于0")
    private Integer deptId;

    @Schema(description = "职级ID")
    @NotNull(message = "职级ID不能为空")
    @Min(value = 1, message = "职级ID必须大于0")
    private Integer levelId;

    @Schema(description = "岗位ID")
    @NotNull(message = "岗位ID不能为空")
    @Min(value = 1, message = "岗位ID必须大于0")
    private Integer positionId;

    @Schema(description = "行政区划")
    @NotEmpty(message = "行政区划不能为空")
    private String[] city;

    @Schema(description = "详细地址")
    @NotBlank(message = "详细地址不能为空")
    @Size(max = 255, message = "详细地址最多255个字符")
    private String address;

    @Xss(message = "登录账号不能包含脚本字符")
    @Schema(description = "登录账号")
    @NotBlank(message = "登录账号不能为空")
    @Size(max = 50, message = "登录账号最多50个字符")
    private String username;

    @Xss(message = "登录密码不能包含脚本字符")
    @Schema(description = "登录密码")
    private String password;

    @Schema(description = "加密盐")
    private String salt;

    @Schema(description = "个人简介")
    @Size(max = 500, message = "个人简介最多500个字符")
    private String intro;

    @Schema(description = "用户状态：1-正常 2-禁用")
    @NotNull(message = "用户状态不能为空")
    @Range(min = 1, max = 2, message = "用户状态值在1-2之间")
    private Integer status;

    @Schema(description = "用户备注")
    @Size(max = 500, message = "用户备注最多500个字符")
    private String note;

    @Schema(description = "用户排序")
    @NotNull(message = "用户排序不能为空")
    @Min(value = 0, message = "排序不得小于0")
    private Integer sort;

    @Schema(description = "用户角色ID集合")
    @NotEmpty(message = "用户角色ID集合不能为空")
    private Integer[] roles;

}
