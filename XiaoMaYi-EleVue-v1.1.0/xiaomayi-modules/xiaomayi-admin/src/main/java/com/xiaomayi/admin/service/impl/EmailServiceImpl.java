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

package com.xiaomayi.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xiaomayi.admin.service.EmailService;
import com.xiaomayi.core.utils.R;
import com.xiaomayi.core.utils.StringUtils;
import com.xiaomayi.email.annotation.EmailLog;
import com.xiaomayi.email.dto.EmailSendDTO;
import com.xiaomayi.email.enums.EmailType;
import com.xiaomayi.email.utils.EmailUtils;
import com.xiaomayi.system.entity.EmailTemplate;
import com.xiaomayi.system.service.EmailTemplateService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final EmailTemplateService emailTemplateService;
    private final EmailUtils emailUtils;

    /**
     * 发送邮件
     *
     * @param emailSendDTO 参数
     * @return 返回结果
     */
    @EmailLog(title = "注册邮件", type = EmailType.REGISTER)
    @Override
    public R sendEmail(EmailSendDTO emailSendDTO) {
        // 根据模板ID查询
        EmailTemplate emailTemplate = emailTemplateService.getOne(new LambdaQueryWrapper<EmailTemplate>()
                .eq(EmailTemplate::getCode, emailSendDTO.getCode())
                .eq(EmailTemplate::getDelFlag, 0)
                .orderByDesc(EmailTemplate::getId), false);
        if (StringUtils.isNotNull(emailTemplate)) {
            // 模板ID
            emailSendDTO.setTemplateId(emailTemplate.getId());
            // 模板类型
            emailSendDTO.setType(emailTemplate.getType());
            // 模板标题
            if (StringUtils.isEmpty(emailSendDTO.getTitle())) {
                emailSendDTO.setTitle(emailTemplate.getTitle());
            }
            // 模板内容
            if (StringUtils.isEmpty(emailSendDTO.getContent())) {
                emailSendDTO.setContent(emailTemplate.getContent());
            }
            // 模板文件路径
            if (StringUtils.isNotEmpty(emailTemplate.getFilePath())) {
                emailSendDTO.setTplPath(emailTemplate.getFilePath());
            }
        }
        // 邮件标题判空
        if (StringUtils.isEmpty(emailSendDTO.getTitle())) {
            return R.failed("邮件标题不能为空");
        }

        // 发送邮件模板
        boolean result = emailUtils.sendEMail(emailSendDTO);
        if (!result) {
            return R.failed("邮件发送失败");
        }
        // 返回结果
        return R.ok();
    }
}
