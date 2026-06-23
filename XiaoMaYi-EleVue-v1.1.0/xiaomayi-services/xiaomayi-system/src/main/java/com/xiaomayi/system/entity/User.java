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

package com.xiaomayi.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.xiaomayi.mybatis.model.TenantEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

/**
 * <p>
 * 用户
 * </p>
 *
 * @author 小蚂蚁云团队
 * @since 2024-03-23
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@TableName("sys_user")
@Schema(name = "用户", description = "用户")
public class User extends TenantEntity {

    @Schema(description = "用户ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @Schema(description = "用户编码")
    private String code;

    @Schema(description = "用户名称")
    private String realname;

    @Schema(description = "用户性别：1-男 2-女 3-保密")
    private Integer gender;

    @Schema(description = "用户头像")
    private String avatar;

    @Schema(description = "手机号码")
    private String mobile;

    @Schema(description = "邮箱地址")
    private String email;

    @Schema(description = "用户类型：0-系统用户 1-租户用户")
    private Integer type;

    @Schema(description = "部门ID")
    private Integer deptId;

    @Schema(description = "职级ID")
    private Integer levelId;

    @Schema(description = "岗位ID")
    private Integer positionId;

    @Schema(description = "省份编码")
    private String provinceCode;

    @Schema(description = "市区编码")
    private String cityCode;

    @Schema(description = "区县编码")
    private String districtCode;

    @Schema(description = "街道编码")
    private String streetCode;

    @Schema(description = "城市信息")
    private String cityInfo;

    @Schema(description = "详细地址")
    private String address;

    @Schema(description = "登录账号")
    private String username;

    @Schema(description = "登录密码")
    @JsonIgnore
    private String password;

    @Schema(description = "加密盐")
    @JsonIgnore
    private String salt;

    @Schema(description = "个人简介")
    private String intro;

    @Schema(description = "用户状态：1-正常 2-禁用")
    private Integer status;

    @Schema(description = "用户备注")
    private String note;

    @Schema(description = "用户排序")
    private Integer sort;

    @Schema(description = "是否主账号：1-是 0-否")
    private Integer master;

    @Schema(description = "登录次数")
    private Integer loginNum;

    @Schema(description = "最近登录IP")
    private String loginIp;

    @Schema(description = "最近登录时间")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime loginTime;

}