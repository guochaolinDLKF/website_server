// +----------------------------------------------------------------------
// | 小蚂蚁云企业级开发框架 [ 赋能开发者，助力企业发展 ]
// +----------------------------------------------------------------------
// | 版权所有 2022~2024 小蚂蚁团队
// +----------------------------------------------------------------------
// | Licensed Apache-2.0 【小蚂蚁云】并不是自由软件，未经许可禁止去掉相关版权
// +----------------------------------------------------------------------
// | 官方网站: https://www.xiaomayicloud.com
// +----------------------------------------------------------------------
// | 软件作者: @小蚂蚁团队 团队荣誉出品
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

package com.xiaomayi.admin.entity;

import com.xiaomayi.sensitive.annotation.Sensitive;
import com.xiaomayi.sensitive.enums.SensitiveMode;
import com.xiaomayi.sensitive.enums.SensitiveType;
import lombok.Data;

@Data
public class TestModel {

    /**
     * 模拟密码
     */
    @Sensitive(type = SensitiveType.PASSWORD)
    private String password;
    /**
     * 模拟邮箱
     */
    @Sensitive(type = SensitiveType.EMAIL)
    private String email;
    /**
     * 模拟手机号
     */
    @Sensitive(type = SensitiveType.MOBILE_PHONE)
    private String phone;
    /**
     * 模拟座机
     */
    @Sensitive(type = SensitiveType.FIXED_PHONE)
    private String fixPhone;
    /**
     * 模拟银行卡
     */
    @Sensitive(type = SensitiveType.BANK_CARD)
    private String bankCard;
    /**
     * 模拟身份证号
     */
    @Sensitive(type = SensitiveType.ID_CARD)
    private String idCard;
    /**
     * 模拟中文名
     */
    @Sensitive(type = SensitiveType.CHINESE_NAME)
    private String name;
    /**
     * 模拟住址
     */
    @Sensitive(type = SensitiveType.ADDRESS)
    private String address;
    /**
     * 模拟自定义脱敏-头部脱敏
     */
    @Sensitive(type = SensitiveType.DEFAULT, mode = SensitiveMode.HEAD, tailNoMaskLen = 4)
    private String headStr;
    /**
     * 模拟自定义脱敏-尾部脱敏
     */
    @Sensitive(type = SensitiveType.DEFAULT, mode = SensitiveMode.TAIL, headNoMaskLen = 4)
    private String tailStr;
    /**
     * 模拟自定义脱敏-中间脱敏
     */
    @Sensitive(type = SensitiveType.DEFAULT, mode = SensitiveMode.MIDDLE, headNoMaskLen = 2, tailNoMaskLen = 2)
    private String middleStr;
    /**
     * 模拟自定义脱敏-两头脱敏,设置中间不脱敏长度
     */
    @Sensitive(type = SensitiveType.DEFAULT, mode = SensitiveMode.HEAD_TAIL, middleNoMaskLen = 4)
    private String headTailStr;
    /**
     * 模拟自定义脱敏-全部脱敏
     */
    @Sensitive(type = SensitiveType.DEFAULT, mode = SensitiveMode.ALL)
    private String allStr;
    /**
     * 模拟自定义脱敏-不脱敏
     */
    @Sensitive(type = SensitiveType.DEFAULT, mode = SensitiveMode.NONE)
    private String noneStr;

}
