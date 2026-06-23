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
import com.xiaomayi.core.utils.R;
import com.xiaomayi.core.utils.StringUtils;
import com.xiaomayi.system.dto.messagetemplate.MessageTemplateAddDTO;
import com.xiaomayi.system.dto.messagetemplate.MessageTemplatePageDTO;
import com.xiaomayi.system.dto.messagetemplate.MessageTemplateUpdateDTO;
import com.xiaomayi.system.entity.MessageTemplate;
import com.xiaomayi.system.mapper.MessageTemplateMapper;
import com.xiaomayi.system.service.MessageTemplateService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiaomayi.system.vo.messagetemplate.MessageTemplateInfoVO;
import com.xiaomayi.system.vo.messagetemplate.MessageTemplateListVO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 消息模板 服务实现类
 * </p>
 *
 * @author 小蚂蚁云团队
 * @since 2024-08-06
 */
@Service
public class MessageTemplateServiceImpl extends ServiceImpl<MessageTemplateMapper, MessageTemplate> implements MessageTemplateService {

    /**
     * 查询分页列表
     *
     * @param messageTemplatePageDTO 查询条件
     * @return 返回结果
     */
    @Override
    public Page<MessageTemplate> page(MessageTemplatePageDTO messageTemplatePageDTO) {
        // 分页设置
        Page<MessageTemplate> page = new Page<>(messageTemplatePageDTO.getPageNo(), messageTemplatePageDTO.getPageSize());
        // 查询条件
        LambdaQueryWrapper<MessageTemplate> wrapper = new LambdaQueryWrapper<MessageTemplate>()
                // 模板标题
                .like(StringUtils.isNotEmpty(messageTemplatePageDTO.getTitle()), MessageTemplate::getTitle, messageTemplatePageDTO.getTitle())
                // 消息类型：1-系统通知 2-用户私信 3-代办事项
                .eq(StringUtils.isNotNull(messageTemplatePageDTO.getType()) && messageTemplatePageDTO.getType() > 0, MessageTemplate::getType, messageTemplatePageDTO.getType())
                .eq(MessageTemplate::getDelFlag, 0)
                .orderByAsc(MessageTemplate::getId);
        // 查询分页数据
        Page<MessageTemplate> pageData = page(page, wrapper);
        // 遍历数据源
        pageData.convert(item -> {
            // 实例化VO对象
            MessageTemplateListVO messageTemplateListVO = new MessageTemplateListVO();
            BeanUtils.copyProperties(item, messageTemplateListVO);
            return messageTemplateListVO;
        });
        // 返回结果
        return pageData;
    }

    /**
     * 根据ID查询信息
     *
     * @param id 消息模板ID
     * @return 返回结果
     */
    @Override
    public MessageTemplate getInfo(Integer id) {
        MessageTemplate messageTemplate = getById(id);
        if (StringUtils.isNull(messageTemplate) || !messageTemplate.getDelFlag().equals(0)) {
            return null;
        }
        return messageTemplate;
    }

    /**
     * 根据ID查询详情
     *
     * @param id 消息模板ID
     * @return 返回结果
     */
    @Override
    public MessageTemplateInfoVO getDetail(Integer id) {
        MessageTemplate messageTemplate = getInfo(id);
        if (StringUtils.isNull(messageTemplate)) {
            return null;
        }
        // 实例化VO
        MessageTemplateInfoVO messageTemplateInfoVO = new MessageTemplateInfoVO();
        BeanUtils.copyProperties(messageTemplate, messageTemplateInfoVO);
        return messageTemplateInfoVO;
    }

    /**
     * 添加消息模板
     *
     * @param messageTemplateAddDTO 参数
     * @return 返回结果
     */
    @Override
    public R add(MessageTemplateAddDTO messageTemplateAddDTO) {
        // 实例化对象
        MessageTemplate messageTemplate = new MessageTemplate();
        // 属性拷贝
        BeanUtils.copyProperties(messageTemplateAddDTO, messageTemplate);
        boolean result = save(messageTemplate);
        if (!result) {
            return R.failed();
        }
        return R.ok();
    }

    /**
     * 更新消息模板
     *
     * @param messageTemplateUpdateDTO 参数
     * @return 返回结果
     */
    @Override
    public R update(MessageTemplateUpdateDTO messageTemplateUpdateDTO) {
        // 根据ID查询信息
        MessageTemplate messageTemplate = getInfo(messageTemplateUpdateDTO.getId());
        if (StringUtils.isNull(messageTemplate)) {
            return R.failed("记录不存在");
        }
        // 属性拷贝
        BeanUtils.copyProperties(messageTemplateUpdateDTO, messageTemplate);
        boolean result = updateById(messageTemplate);
        if (!result) {
            return R.failed();
        }
        return R.ok();
    }

    /**
     * 删除消息模板
     *
     * @param id 消息模板ID
     * @return 返回结果
     */
    @Override
    public R delete(Integer id) {
        // 根据ID查询信息
        MessageTemplate messageTemplate = getInfo(id);
        if (StringUtils.isNull(messageTemplate)) {
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
     * 批量删除消息模板
     *
     * @param idList 消息模板ID
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
     * 根据模板编号查询消息模板
     *
     * @param number 模板编号
     * @return 返回结果
     */
    @Override
    public MessageTemplate getTemplateInfo(String number) {
        MessageTemplate messageTemplate = getOne(new LambdaQueryWrapper<MessageTemplate>()
                .eq(MessageTemplate::getNumber, number)
                .eq(MessageTemplate::getDelFlag, 0), false);
        return messageTemplate;
    }
}
