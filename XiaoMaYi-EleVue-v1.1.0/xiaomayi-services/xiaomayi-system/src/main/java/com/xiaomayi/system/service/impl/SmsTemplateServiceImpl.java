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

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiaomayi.core.utils.R;
import com.xiaomayi.core.utils.StringUtils;
import com.xiaomayi.sms.dto.SmsSendDTO;
import com.xiaomayi.sms.utils.AliyunSmsUtil;
import com.xiaomayi.system.dto.smstemplate.SmsTemplateAddDTO;
import com.xiaomayi.system.dto.smstemplate.SmsTemplatePageDTO;
import com.xiaomayi.system.dto.smstemplate.SmsTemplateUpdateDTO;
import com.xiaomayi.system.entity.SmsTemplate;
import com.xiaomayi.system.mapper.SmsTemplateMapper;
import com.xiaomayi.system.service.SmsTemplateService;
import com.xiaomayi.system.vo.smstemplate.SmsTemplateInfoVO;
import com.xiaomayi.system.vo.smstemplate.SmsTemplateListVO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 短信模板 服务实现类
 * </p>
 *
 * @author 小蚂蚁云团队
 * @since 2024-06-07
 */
@Service
public class SmsTemplateServiceImpl extends ServiceImpl<SmsTemplateMapper, SmsTemplate> implements SmsTemplateService {

    /**
     * 查询分页列表
     *
     * @param smsTemplatePageDTO 查询条件
     * @return 返回结果
     */
    @Override
    public Page<SmsTemplate> page(SmsTemplatePageDTO smsTemplatePageDTO) {
        // 分页设置
        Page<SmsTemplate> page = new Page<>(smsTemplatePageDTO.getPageNo(), smsTemplatePageDTO.getPageSize());
        // 查询条件
        LambdaQueryWrapper<SmsTemplate> wrapper = new LambdaQueryWrapper<SmsTemplate>()
                // 模板标题
                .like(StringUtils.isNotEmpty(smsTemplatePageDTO.getTitle()), SmsTemplate::getTitle, smsTemplatePageDTO.getTitle())
                // 模板类型：1-阿里云 2-腾讯云 3-华为云
                .eq(StringUtils.isNotNull(smsTemplatePageDTO.getType()) && smsTemplatePageDTO.getType() > 0, SmsTemplate::getType, smsTemplatePageDTO.getType())
                .eq(SmsTemplate::getDelFlag, 0)
                .orderByAsc(SmsTemplate::getId);
        // 查询分页数据
        Page<SmsTemplate> pageData = page(page, wrapper);
        // 遍历数据源
        pageData.convert(item -> {
            // 实例化VO对象
            SmsTemplateListVO smsTemplateListVO = new SmsTemplateListVO();
            BeanUtils.copyProperties(item, smsTemplateListVO);
            return smsTemplateListVO;
        });
        // 返回结果
        return pageData;
    }

    /**
     * 根据ID查询信息
     *
     * @param id 短信模板ID
     * @return 返回结果
     */
    @Override
    public SmsTemplate getInfo(Integer id) {
        SmsTemplate smsTemplate = getById(id);
        if (StringUtils.isNull(smsTemplate) || !smsTemplate.getDelFlag().equals(0)) {
            return null;
        }
        return smsTemplate;
    }

    /**
     * 根据ID查询详情
     *
     * @param id 短信模板ID
     * @return 返回结果
     */
    @Override
    public SmsTemplateInfoVO getDetail(Integer id) {
        SmsTemplate smsTemplate = getInfo(id);
        if (StringUtils.isNull(smsTemplate)) {
            return null;
        }
        // 实例化VO
        SmsTemplateInfoVO smsTemplateInfoVO = new SmsTemplateInfoVO();
        BeanUtils.copyProperties(smsTemplate, smsTemplateInfoVO);
        return smsTemplateInfoVO;
    }

    /**
     * 添加短信模板
     *
     * @param smsTemplateAddDTO 参数
     * @return 返回结果
     */
    @Override
    public R add(SmsTemplateAddDTO smsTemplateAddDTO) {
        // 检查模板编号是否已存在
        if (checkExist(smsTemplateAddDTO.getNumber(), 0)) {
            return R.failed("模板编号已存在");
        }
        // 实例化对象
        SmsTemplate smsTemplate = new SmsTemplate();
        // 属性拷贝
        BeanUtils.copyProperties(smsTemplateAddDTO, smsTemplate);
        boolean result = save(smsTemplate);
        if (!result) {
            return R.failed();
        }
        return R.ok();
    }

    /**
     * 更新短信模板
     *
     * @param smsTemplateUpdateDTO 参数
     * @return 返回结果
     */
    @Override
    public R update(SmsTemplateUpdateDTO smsTemplateUpdateDTO) {
        // 根据ID查询信息
        SmsTemplate smsTemplate = getInfo(smsTemplateUpdateDTO.getId());
        if (StringUtils.isNull(smsTemplate)) {
            return R.failed("记录不存在");
        }
        // 检查模板编号是否已存在
        if (checkExist(smsTemplateUpdateDTO.getNumber(), smsTemplateUpdateDTO.getId())) {
            return R.failed("模板编号已存在");
        }
        // 属性拷贝
        BeanUtils.copyProperties(smsTemplateUpdateDTO, smsTemplate);
        boolean result = updateById(smsTemplate);
        if (!result) {
            return R.failed();
        }
        return R.ok();
    }

    /**
     * 删除短信模板
     *
     * @param idList 短信模板ID
     * @return 返回结果
     */
    @Override
    public R delete(List<Integer> idList) {
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
     * 检查模板编号是否已存在
     *
     * @param number 模板编号
     * @param id     模板ID
     * @return 返回结果
     */
    @Override
    public boolean checkExist(String number, Integer id) {
        SmsTemplate smsTemplate = getOne(new LambdaQueryWrapper<SmsTemplate>()
                .eq(SmsTemplate::getNumber, number)
                .ne(id > 0, SmsTemplate::getId, id)
                .eq(SmsTemplate::getDelFlag, 0), false);
        return StringUtils.isNotNull(smsTemplate);
    }

    /**
     * 根据模板编号获取短信模板
     *
     * @param number 模板编号
     * @return 返回结果
     */
    @Override
    public SmsTemplate getSmsTemplate(String number) {
        SmsTemplate smsTemplate = getOne(new LambdaQueryWrapper<SmsTemplate>()
                .eq(SmsTemplate::getNumber, number)
                .eq(SmsTemplate::getDelFlag, 0), false);
        return smsTemplate;
    }

    /**
     * 发送短信
     *
     * @param smsSendDTO 参数
     * @return 返回结果
     */
    @Override
    public R sendSms(SmsSendDTO smsSendDTO) {
        // 短信模板编号
        String number = smsSendDTO.getNumber();
        // 手机号
        String mobile = smsSendDTO.getMobile();
        // 短信参数
        String params = JSON.toJSONString(smsSendDTO.getParams());
        // 查询短信模板
        SmsTemplate smsTemplate = getSmsTemplate(number);
        if (StringUtils.isNull(smsTemplate)) {
            return R.failed("短信模板不存在");
        }
        // 模板ID
        smsSendDTO.setTemplateId(smsTemplate.getId());
        // 发送短信
        return AliyunSmsUtil.sendSms(mobile, params);
    }

}
