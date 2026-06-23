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

package com.xiaomayi.admin.controller.demo;

import com.xiaomayi.admin.service.EmailService;
import com.xiaomayi.core.utils.R;
import com.xiaomayi.email.dto.EmailSendDTO;
import com.xiaomayi.email.utils.EmailUtils;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 邮件发送 前端控制器
 * </p>
 *
 * @author 小蚂蚁团队
 * @since 2024-05-26
 */
@RestController
@RequestMapping("/email")
@AllArgsConstructor
public class EmailController {

    private final EmailUtils emailUtils;

    private final EmailService emailService;

    /**
     * 发送邮件
     *
     * @return 返回结果
     */
    @PostMapping("/sendEmail")
    public R sendSms() {
        try {
            String receiveEmail = "12345678@qq.com";

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
            params2.put("username", "阿苏勒");
            params2.put("code", "123456");
            emailSendDTO3.setParams(params2);

            // 附件处理
            List<String> fileList = new ArrayList<>();
            fileList.add("D:\\test.html");
            emailSendDTO3.setAttachment(fileList);

            emailService.sendEmail(emailSendDTO3);


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
//            emailUtils.sendInlineMail("12345678@qq.com", "发送邮件测试", content2, rscPath, rscId);

//            // 发送模板邮件
//            Map<String, Object> map = new HashMap<>();
//            map.put("username", "收件人姓名");
//            map.put("code", "12345678");
//            emailUtils.sendTemplateMail(receiveEmail, "这是一个模板文件", "test.html", map);
        } catch (Exception e) {

        }

        return R.ok();
    }

}
