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

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xiaomayi.core.utils.R;
import com.xiaomayi.core.utils.StringUtils;
import com.xiaomayi.system.dto.message.*;
import com.xiaomayi.system.entity.Message;
import com.xiaomayi.system.entity.MessageTemplate;
import com.xiaomayi.system.mapper.MessageMapper;
import com.xiaomayi.system.service.MessageService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiaomayi.system.service.MessageTemplateService;
import com.xiaomayi.system.vo.message.MessageInfoVO;
import com.xiaomayi.system.vo.message.MessageListVO;
import com.xiaomayi.websocket.server.WebSocketServer;
import lombok.AllArgsConstructor;
import org.apache.commons.text.StringSubstitutor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 消息 服务实现类
 * </p>
 *
 * @author 小蚂蚁云团队
 * @since 2024-08-06
 */
@Service
@AllArgsConstructor
public class MessageServiceImpl extends ServiceImpl<MessageMapper, Message> implements MessageService {

    private final MessageTemplateService messageTemplateService;

    /**
     * 查询分页列表
     *
     * @param messagePageDTO 查询条件
     * @return 返回结果
     */
    @Override
    public Page<Message> page(MessagePageDTO messagePageDTO) {
        // 分页设置
        Page<Message> page = new Page<>(messagePageDTO.getPageNo(), messagePageDTO.getPageSize());
        // 查询条件
        LambdaQueryWrapper<Message> wrapper = new LambdaQueryWrapper<Message>()
                // 消息标题
                .like(StringUtils.isNotEmpty(messagePageDTO.getTitle()), Message::getTitle, messagePageDTO.getTitle())
                // 接收人ID
                .eq(StringUtils.isNotNull(messagePageDTO.getUserId()) && messagePageDTO.getUserId() > 0, Message::getUserId, messagePageDTO.getUserId())
                // 消息类型：1-系统通知 2-用户私信 3-代办事项
                .eq(StringUtils.isNotNull(messagePageDTO.getType()) && messagePageDTO.getType() > 0, Message::getType, messagePageDTO.getType())
                // 业务类型：1-订单 2-其他
                .eq(StringUtils.isNotNull(messagePageDTO.getBizType()) && messagePageDTO.getBizType() > 0, Message::getBizType, messagePageDTO.getBizType())
                // 消息状态：0-未读 1-已读
                .eq(StringUtils.isNotNull(messagePageDTO.getStatus()) && messagePageDTO.getStatus() > 0, Message::getStatus, messagePageDTO.getStatus())
                .eq(Message::getDelFlag, 0)
                .orderByAsc(Message::getId);
        // 查询分页数据
        Page<Message> pageData = page(page, wrapper);
        // 遍历数据源
        pageData.convert(item -> {
            // 实例化VO对象
            MessageListVO messageListVO = new MessageListVO();
            BeanUtils.copyProperties(item, messageListVO);
            return messageListVO;
        });
        // 返回结果
        return pageData;
    }

    /**
     * 根据ID查询信息
     *
     * @param id 消息ID
     * @return 返回结果
     */
    @Override
    public Message getInfo(Integer id) {
        Message message = getById(id);
        if (StringUtils.isNull(message) || !message.getDelFlag().equals(0)) {
            return null;
        }
        return message;
    }

    /**
     * 根据ID查询详情
     *
     * @param id 消息ID
     * @return 返回结果
     */
    @Override
    public MessageInfoVO getDetail(Integer id) {
        Message message = getInfo(id);
        if (StringUtils.isNull(message)) {
            return null;
        }
        // 实例化VO
        MessageInfoVO messageInfoVO = new MessageInfoVO();
        BeanUtils.copyProperties(message, messageInfoVO);
        return messageInfoVO;
    }

    /**
     * 添加消息
     *
     * @param messageAddDTO 参数
     * @return 返回结果
     */
    @Override
    public R add(MessageAddDTO messageAddDTO) {
        // 实例化对象
        Message message = new Message();
        // 属性拷贝
        BeanUtils.copyProperties(messageAddDTO, message);
        boolean result = save(message);
        if (!result) {
            return R.failed();
        }
        return R.ok();
    }

    /**
     * 更新消息
     *
     * @param messageUpdateDTO 参数
     * @return 返回结果
     */
    @Override
    public R update(MessageUpdateDTO messageUpdateDTO) {
        // 根据ID查询信息
        Message message = getInfo(messageUpdateDTO.getId());
        if (StringUtils.isNull(message)) {
            return R.failed("记录不存在");
        }
        // 属性拷贝
        BeanUtils.copyProperties(messageUpdateDTO, message);
        boolean result = updateById(message);
        if (!result) {
            return R.failed();
        }
        return R.ok();
    }

    /**
     * 删除消息
     *
     * @param id 消息ID
     * @return 返回结果
     */
    @Override
    public R delete(Integer id) {
        // 根据ID查询信息
        Message message = getInfo(id);
        if (StringUtils.isNull(message)) {
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
     * 批量删除消息
     *
     * @param idList 消息ID
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
     * 发送消息
     *
     * @param messageSendDTO 参数
     * @return 返回结果
     */
    @Override
    public R sendMessage(MessageSendDTO messageSendDTO) {
        // 根据模板编号查询模板
        MessageTemplate messageTemplate = messageTemplateService.getTemplateInfo(messageSendDTO.getNumber());
        if (StringUtils.isNull(messageTemplate)) {
            return R.failed("消息模板不存在");
        }
        // 实例化消息添加DTO
        Message message = new Message();
        message.setNumber(messageTemplate.getNumber());
        message.setTitle(messageTemplate.getTitle());
        message.setType(messageTemplate.getType());
        message.setUserId(messageSendDTO.getUserId());
        message.setParam(messageSendDTO.getParam());

        // 消息模板内容
        String content = messageTemplate.getContent();
        // 模板参数
        Map<String, Object> params = JSONObject.parseObject(messageSendDTO.getParam());
        // 消息参数格式化
        if (StringUtils.isNotEmpty(content)) {
            StringSubstitutor stringSubstitutor = new StringSubstitutor(params);
            // 邮件内容参数替换，此处基于依赖commons-text
            content = stringSubstitutor.replace(content);
            message.setContent(stringSubstitutor.replace(content));
        }
        message.setBizType(messageSendDTO.getBizType());
        message.setBizId(messageSendDTO.getBizId());
        message.setBizContent(messageSendDTO.getBizContent());
        // 消息状态：0-未读 1-已读
        message.setStatus(0);
        // 发送日期
        message.setSendTime(LocalDateTime.now());
        // 保存消息内容
        boolean result = save(message);
        if (!result) {
            return R.failed();
        }
        // 对接WebSocket推送消息给接收方
        try {
            WebSocketServer.sendInfo(message.getContent(), message.getUserId().toString());
        } catch (Exception e) {
            log.error("Socket消息发送失败：{}", e);
        }
        return R.ok();
    }

    /**
     * 我的消息
     *
     * @param messagePageDTO 查询条件
     * @return 返回结果
     */
    @Override
    public R profile(MessagePageDTO messagePageDTO) {
        // 查询条件
        LambdaQueryWrapper<Message> wrapper = new LambdaQueryWrapper<Message>()
                // 接收人ID
                .eq(Message::getUserId, messagePageDTO.getUserId())
                // 消息类型：1-系统通知 2-用户私信 3-代办事项
                .eq(Message::getType, messagePageDTO.getType())
                .eq(Message::getDelFlag, 0)
                // 未读消息靠前
                .orderByAsc(Message::getStatus)
                .orderByDesc(Message::getId);

        // 分页设置
        Page<Message> page = new Page<>(messagePageDTO.getPageNo(), messagePageDTO.getPageSize());
        // 查询分页数据
        Page<Message> pageData = page(page, wrapper);
        pageData.convert(item -> {
            // 实例化VO对象
            MessageListVO messageListVO = new MessageListVO();
            BeanUtils.copyProperties(item, messageListVO);
            return messageListVO;
        });

        // 查询系统消息未读数量
        LambdaQueryWrapper<Message> queryWrapper1 = new LambdaQueryWrapper<Message>()
                .eq(Message::getUserId, messagePageDTO.getUserId())
                .eq(Message::getType, 1)
                .eq(Message::getStatus, 0)
                .eq(Message::getDelFlag, 0);
        long systemNum = count(queryWrapper1);
        // 查询用户私信未读数量
        LambdaQueryWrapper<Message> queryWrapper2 = new LambdaQueryWrapper<Message>()
                .eq(Message::getUserId, messagePageDTO.getUserId())
                .eq(Message::getType, 2)
                .eq(Message::getStatus, 0)
                .eq(Message::getDelFlag, 0);
        long profileNum = count(queryWrapper2);
        // 查询代办事项未读数量
        LambdaQueryWrapper<Message> queryWrapper3 = new LambdaQueryWrapper<Message>()
                .eq(Message::getUserId, messagePageDTO.getUserId())
                .eq(Message::getType, 3)
                .eq(Message::getStatus, 0)
                .eq(Message::getDelFlag, 0);
        long projectNum = count(queryWrapper3);

        // 封装返回结构体
        Map<String, Object> result = new HashMap<>();
        result.put("total", pageData.getTotal());
        result.put("size", pageData.getSize());
        result.put("current", pageData.getCurrent());
        result.put("pages", pageData.getPages());
        result.put("records", pageData.getRecords());
        result.put("systemNum", systemNum);
        result.put("profileNum", profileNum);
        result.put("projectNum", projectNum);

        // 返回结果
        return R.ok(result);
    }

    /**
     * 设置已读
     *
     * @param messageReadDTO 参数
     * @return 返回结果
     */
    @Override
    public R setRead(MessageReadDTO messageReadDTO) {
        // 标记已读计数器
        Integer totalNum = 0;
        // 消息ID列表
        List<Integer> idList = messageReadDTO.getIdList();
        // 遍历消息列表
        for (Integer id : idList) {
            // 查询消息
            Message message = getInfo(id);
            if (StringUtils.isNull(message)) {
                continue;
            }
            // 判断是否当前用户消息
            if (!message.getUserId().equals(messageReadDTO.getUserId())) {
                continue;
            }
            // 设置已读状态
            message.setStatus(1);
            boolean result = updateById(message);
            if (!result) {
                continue;
            }
            // 计数器+1
            totalNum += 1;
        }
        // 返回结果
        return R.ok(null, String.format("本次共计标记【%d】条消息为已读状态！", totalNum));
    }
}