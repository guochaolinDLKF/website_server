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

package com.xiaomayi.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiaomayi.core.utils.R;
import com.xiaomayi.core.utils.StringUtils;
import com.xiaomayi.email.annotation.EmailLog;
import com.xiaomayi.email.dto.EmailSendDTO;
import com.xiaomayi.email.enums.EmailType;
import com.xiaomayi.email.utils.EmailUtils;
import com.xiaomayi.system.dto.emailtemplate.EmailTemplateAddDTO;
import com.xiaomayi.system.dto.emailtemplate.EmailTemplatePageDTO;
import com.xiaomayi.system.dto.emailtemplate.EmailTemplateUpdateDTO;
import com.xiaomayi.system.entity.EmailTemplate;
import com.xiaomayi.system.mapper.EmailTemplateMapper;
import com.xiaomayi.system.service.EmailTemplateService;
import com.xiaomayi.system.vo.emailtemplate.EmailTemplateInfoVO;
import com.xiaomayi.system.vo.emailtemplate.EmailTemplateListVO;
import lombok.AllArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 邮件模板 服务实现类
 * </p>
 *
 * @author 小蚂蚁云团队
 * @since 2024-06-16
 */
@Service
@AllArgsConstructor
public class EmailTemplateServiceImpl extends ServiceImpl<EmailTemplateMapper, EmailTemplate> implements EmailTemplateService {

    private final EmailUtils emailUtils;

    /**
     * 查询分页列表
     *
     * @param emailTemplatePageDTO 查询条件
     * @return 返回结果
     */
    @Override
    public Page<EmailTemplate> page(EmailTemplatePageDTO emailTemplatePageDTO) {
        // 分页设置
        Page<EmailTemplate> page = new Page<>(emailTemplatePageDTO.getPageNo(), emailTemplatePageDTO.getPageSize());
        // 查询条件
        LambdaQueryWrapper<EmailTemplate> wrapper = new LambdaQueryWrapper<EmailTemplate>()
                // 模板标题
                .like(StringUtils.isNotEmpty(emailTemplatePageDTO.getTitle()), EmailTemplate::getTitle, emailTemplatePageDTO.getTitle())
                // 邮件类型：1-普通邮件 2-图文邮件 3-模板文件
                .eq(StringUtils.isNotNull(emailTemplatePageDTO.getType()) && emailTemplatePageDTO.getType() > 0, EmailTemplate::getType, emailTemplatePageDTO.getType())
                .eq(EmailTemplate::getDelFlag, 0)
                .orderByAsc(EmailTemplate::getId);
        // 查询分页数据
        Page<EmailTemplate> pageData = page(page, wrapper);
        // 遍历数据源
        pageData.convert(item -> {
            // 实例化VO对象
            EmailTemplateListVO emailTemplateListVO = new EmailTemplateListVO();
            BeanUtils.copyProperties(item, emailTemplateListVO);
            return emailTemplateListVO;
        });
        // 返回结果
        return pageData;
    }

    /**
     * 根据ID查询信息
     *
     * @param id 邮件模板ID
     * @return 返回结果
     */
    @Override
    public EmailTemplate getInfo(Integer id) {
        EmailTemplate emailTemplate = getById(id);
        if (StringUtils.isNull(emailTemplate) || !emailTemplate.getDelFlag().equals(0)) {
            return null;
        }
        return emailTemplate;
    }

    /**
     * 根据ID查询详情
     *
     * @param id 邮件模板ID
     * @return 返回结果
     */
    @Override
    public EmailTemplateInfoVO getDetail(Integer id) {
        EmailTemplate emailTemplate = getInfo(id);
        if (StringUtils.isNull(emailTemplate)) {
            return null;
        }
        // 实例化VO
        EmailTemplateInfoVO emailTemplateInfoVO = new EmailTemplateInfoVO();
        BeanUtils.copyProperties(emailTemplate, emailTemplateInfoVO);
        return emailTemplateInfoVO;
    }

    /**
     * 添加邮件模板
     *
     * @param emailTemplateAddDTO 参数
     * @return 返回结果
     */
    @Override
    public R add(EmailTemplateAddDTO emailTemplateAddDTO) {
        // 实例化对象
        EmailTemplate emailTemplate = new EmailTemplate();
        // 属性拷贝
        BeanUtils.copyProperties(emailTemplateAddDTO, emailTemplate);
        boolean result = save(emailTemplate);
        if (!result) {
            return R.failed();
        }
        return R.ok();
    }

    /**
     * 更新邮件模板
     *
     * @param emailTemplateUpdateDTO 参数
     * @return 返回结果
     */
    @Override
    public R update(EmailTemplateUpdateDTO emailTemplateUpdateDTO) {
        // 根据ID查询信息
        EmailTemplate emailTemplate = getInfo(emailTemplateUpdateDTO.getId());
        if (StringUtils.isNull(emailTemplate)) {
            return R.failed("记录不存在");
        }
        // 属性拷贝
        BeanUtils.copyProperties(emailTemplateUpdateDTO, emailTemplate);
        boolean result = updateById(emailTemplate);
        if (!result) {
            return R.failed();
        }
        return R.ok();
    }

    /**
     * 删除邮件模板
     *
     * @param id 邮件模板ID
     * @return 返回结果
     */
    @Override
    public R delete(Integer id) {
        // 根据ID查询信息
        EmailTemplate emailTemplate = getInfo(id);
        if (StringUtils.isNull(emailTemplate)) {
            return R.failed("记录不存在");
        }
        // 删除
        boolean result = removeById(id);
        if (!result) {
            return R.failed();
        }
        // 返回结果
        return R.ok();
    }


    /**
     * 批量删除邮件模板
     *
     * @param idList 邮件模板ID
     * @return 返回结果
     */
    @Override
    public R batchDelete(List<Integer> idList) {
        // 删除ID判空
        if (StringUtils.isEmpty(idList)) {
            return R.failed("删除记录ID不存在");
        }
        // 批量删除
        boolean result = removeBatchByIds(idList);
        if (!result) {
            return R.failed();
        }
        return R.ok();
    }

    /**
     * 发送邮件
     *
     * @param emailSendDTO 参数
     * @return 返回结果
     */
    @Override
    public R sendEmail(EmailSendDTO emailSendDTO) {
        // 根据模板ID查询
        EmailTemplate emailTemplate = getOne(new LambdaQueryWrapper<EmailTemplate>()
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
