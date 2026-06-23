package com.xiaomayi.admin.controller.demo;

import com.alibaba.fastjson2.JSON;
import com.xiaomayi.core.utils.R;
import com.xiaomayi.email.annotation.EmailLog;
import com.xiaomayi.email.dto.EmailSendDTO;
import com.xiaomayi.email.enums.EmailType;
import com.xiaomayi.sms.annotation.SmsLog;
import com.xiaomayi.sms.dto.SmsSendDTO;
import com.xiaomayi.sms.enums.SmsType;
import com.xiaomayi.system.service.EmailTemplateService;
import com.xiaomayi.system.service.SmsTemplateService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 案例测试 前端控制器
 * </p>
 *
 * @author 小蚂蚁云团队
 * @since 2024-05-26
 */
@Slf4j
@RestController
@RequestMapping("/example")
@AllArgsConstructor
public class SmsController {

    private final SmsTemplateService smsTemplateService;
    private final EmailTemplateService emailTemplateService;

    /**
     * 短信发送测试
     *
     * @param smsSendDTO 参数
     * @return 返回结果
     */
    @SmsLog(title = "登录短信", type = SmsType.LOGIN)
    @PostMapping("/sendSms")
    public R sendSms(@RequestBody @Validated SmsSendDTO smsSendDTO) {
        // 接收者ID
        smsSendDTO.setReceiveId(1);
        // 接收人类型：1-系统用户 2-会员用户 3-其他
        smsSendDTO.setReceiveType(2);
        // 业务类型
        smsSendDTO.setBizType(1);
        // 业务ID
        smsSendDTO.setBizId(1);
        // 业务内容
        Map<String, Object> bizMap = new HashMap<>();
        bizMap.put("orderId", 1);
        bizMap.put("orderNo", "20240614000001");
        smsSendDTO.setBizContent(JSON.toJSONString(bizMap));

        // 模板参数
        Map<String, Object> map = new HashMap<>();
        map.put("code", "123456");
        smsSendDTO.setParams(map);

        // 发送短信
        return smsTemplateService.sendSms(smsSendDTO);
    }

    /**
     * 邮件发送测试
     *
     * @return 返回结果
     */
    @EmailLog(title = "注册邮件", type = EmailType.REGISTER)
    @PostMapping("/sendEmail")
    public R sendSms() {
        try {
            String receiveEmail = "123456789@qq.com";

//            // 自定纯文本邮件
//            EmailSendDTO emailSendDTO = new EmailSendDTO();
//            emailSendDTO.setEmail(receiveEmail);
//            emailSendDTO.setTitle("邮件发送测试");
//            emailSendDTO.setContent("纯文本邮件测试内容");
//            emailService.sendEmail(emailSendDTO);

//            // 纯文本模板文件
//            EmailSendDTO emailSendDTO2 = new EmailSendDTO();
//            emailSendDTO2.setCode("EMAIL_00001");
//            emailSendDTO2.setEmail(receiveEmail);
//            emailSendDTO2.setTitle("邮件发送测试");
//            // 模板参数
//            Map<String, Object> params = new HashMap<>();
//            params.put("code", "123456");
//            emailSendDTO2.setParams(params);
//            emailService.sendEmail(emailSendDTO2);

            // 模板文件发送
            EmailSendDTO emailSendDTO3 = new EmailSendDTO();
            emailSendDTO3.setCode("EMAIL_00002");
            emailSendDTO3.setEmail(receiveEmail);
//            emailSendDTO3.setTitle("邮件发送测试");
            // 模板参数
            Map<String, Object> params2 = new HashMap<>();
            params2.put("username", "admin");
            params2.put("code", "123456");
            emailSendDTO3.setParams(params2);

            // 附件处理
            List<String> fileList = new ArrayList<>();
            fileList.add("D:\\test.html");
            emailSendDTO3.setAttachment(fileList);

            emailTemplateService.sendEmail(emailSendDTO3);


            System.out.println("11");


//        // 开始发送邮件
//        emailUtils.sendSimpleMail(receiveEmail, "验证码", "123456");

            // 开始发送邮件
//            String content = emailUtils.buildContent("VIP用户", "123456", "email.html");
//            emailUtils.sendMail(receiveEmail, "验证码", content);

//        // 发送带附件邮件
//            File file2 = ResourceUtils.getFile("classpath:email.zip");
//            emailUtils.sendAttachmentMail(receiveEmail, "验证码", content, file2.getPath());

//            File file = ResourceUtils.getFile("classpath:demo.png");
//            String rscId = "001";
//            String rscPath = file.getPath();
//            String content2 = "<html><body style=\"text-align:center;\"><h3><font color=\"red\">" + "大家好，这是springboot发送的HTML邮件，有图片哦" + "</font></h3>"
//                    + "<img src=\'cid:" + rscId + "\'></body></html>";
//            emailUtils.sendInlineMail("123456789@qq.com", "发送邮件测试", content2, rscPath, rscId);

//            // 发送模板邮件
//            Map<String, Object> map = new HashMap<>();
//            map.put("username", "收件人姓名");
//            map.put("code", "12345678");
//            emailUtils.sendTemplateMail(receiveEmail, "这是一个模板文件", "test.html", map);
        } catch (Exception e) {
            log.error("邮件发送异常：{}" + e.getMessage());
        }
        return R.ok();
    }

}
