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

package com.xiaomayi.admin.aop;

import com.alibaba.fastjson2.JSON;
import com.xiaomayi.core.utils.StringUtils;
import com.xiaomayi.security.utils.SecurityUtils;
import com.xiaomayi.sms.aspect.SmsAspect;
import com.xiaomayi.sms.dto.SmsSendDTO;
import com.xiaomayi.sms.vo.SmsLogVO;
import com.xiaomayi.system.entity.SmsLog;
import com.xiaomayi.system.entity.SmsTemplate;
import com.xiaomayi.system.service.SmsLogService;
import com.xiaomayi.system.service.SmsTemplateService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.text.StringSubstitutor;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * <p>
 * 短信日志切面
 * </p>
 *
 * @author 小蚂蚁云团队
 * @since 2024-05-21
 */
@Slf4j
@Aspect
@Component
@AllArgsConstructor
public class SmsLogAspect extends SmsAspect {

    private final SmsLogService smsLogService;
    private final SmsTemplateService smsTemplateService;

    /**
     * 保存短信日志
     *
     * @param smsLogVO 短信日志VO
     * @return 返回结果
     */
    @Override
    public boolean saveSmsLog(SmsLogVO smsLogVO) {
        log.info("发送短信AOP日志处理中...");
        try {
            // 发送短信参数解析
            SmsSendDTO smsSendDTO = JSON.parseObject(smsLogVO.getParam(), SmsSendDTO.class);
            // 模板ID
            Integer templateId = smsLogVO.getTemplateId();
            // 根据模板ID查询短信模板
            SmsTemplate smsTemplate = smsTemplateService.getInfo(templateId);

            // 实例化操作日志对象
            SmsLog smsLog = new SmsLog();
            // 属性拷贝
            BeanUtils.copyProperties(smsLogVO, smsLog);
            // 短信模板判空
            if (StringUtils.isNotNull(smsTemplate)) {
                // 短信标题
                smsLog.setTitle(smsTemplate.getTitle());
                // 短信参数格式化
                StringSubstitutor stringSubstitutor = new StringSubstitutor(smsSendDTO.getParams());
                // 短信内容参数替换，此处基于依赖commons-text
                String content = stringSubstitutor.replace(smsTemplate.getContent());
                // 短信内容
                smsLog.setContent(content);
            }
            // 操作人
            smsLog.setCreateUser(SecurityUtils.getUsername());
            // 操作时间
            smsLog.setCreateTime(LocalDateTime.now());
            // 保存请求日志
            boolean result = smsLogService.save(smsLog);
            // 返回结果
            return result;
        } catch (Exception e) {
            log.error("发送短信AOP日志存储失败：{}", e.getMessage());
        }
        return false;
    }
}
