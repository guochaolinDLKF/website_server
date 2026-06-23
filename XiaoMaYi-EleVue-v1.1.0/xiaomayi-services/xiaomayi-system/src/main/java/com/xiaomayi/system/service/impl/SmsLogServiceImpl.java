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
import com.xiaomayi.system.dto.smslog.SmsLogAddDTO;
import com.xiaomayi.system.dto.smslog.SmsLogPageDTO;
import com.xiaomayi.system.entity.SmsLog;
import com.xiaomayi.system.mapper.SmsLogMapper;
import com.xiaomayi.system.service.SmsLogService;
import com.xiaomayi.system.utils.DictResolver;
import com.xiaomayi.system.vo.smslog.SmsLogInfoVO;
import com.xiaomayi.system.vo.smslog.SmsLogListVO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 短信记录 服务实现类
 * </p>
 *
 * @author 小蚂蚁云团队
 * @since 2024-06-07
 */
@Service
public class SmsLogServiceImpl extends ServiceImpl<SmsLogMapper, SmsLog> implements SmsLogService {

    /**
     * 查询分页列表
     *
     * @param smsLogPageDTO 查询条件
     * @return 返回结果
     */
    @Override
    public Page<SmsLog> page(SmsLogPageDTO smsLogPageDTO) {
        // 分页设置
        Page<SmsLog> page = new Page<>(smsLogPageDTO.getPageNo(), smsLogPageDTO.getPageSize());
        // 查询条件
        LambdaQueryWrapper<SmsLog> wrapper = new LambdaQueryWrapper<SmsLog>()
                // 短信标题
                .like(StringUtils.isNotEmpty(smsLogPageDTO.getTitle()), SmsLog::getTitle, smsLogPageDTO.getTitle())
                // 接收人类型：1-系统用户 2-会员用户 3-其他
                .eq(StringUtils.isNotNull(smsLogPageDTO.getReceiveType()) && smsLogPageDTO.getReceiveType() > 0, SmsLog::getReceiveType, smsLogPageDTO.getReceiveType())
                // 业务类型：1-订单 2-其他
                .eq(StringUtils.isNotNull(smsLogPageDTO.getBizType()) && smsLogPageDTO.getBizType() > 0, SmsLog::getBizType, smsLogPageDTO.getBizType())
                // 状态：0-未读 1-已读
                .eq(StringUtils.isNotNull(smsLogPageDTO.getStatus()) && smsLogPageDTO.getStatus() > 0, SmsLog::getStatus, smsLogPageDTO.getStatus())
                .eq(SmsLog::getDelFlag, 0)
                .orderByDesc(SmsLog::getId);
        // 查询分页数据
        Page<SmsLog> pageData = page(page, wrapper);
        // 遍历数据源
        pageData.convert(item -> {
            // 实例化VO对象
            SmsLogListVO smsLogListVO = new SmsLogListVO();
            BeanUtils.copyProperties(item, smsLogListVO);
            // 日志类型
            Integer type = smsLogListVO.getType();
            if (StringUtils.isNotNull(type)) {
                smsLogListVO.setTypeText(DictResolver.getDictItemName("sms_type", type.toString()));
            }
            return smsLogListVO;
        });
        // 返回结果
        return pageData;
    }

    /**
     * 根据ID查询信息
     *
     * @param id 短信记录ID
     * @return 返回结果
     */
    @Override
    public SmsLog getInfo(Integer id) {
        SmsLog smsLog = getById(id);
        if (StringUtils.isNull(smsLog) || !smsLog.getDelFlag().equals(0)) {
            return null;
        }
        return smsLog;
    }

    /**
     * 根据ID查询详情
     *
     * @param id 短信记录ID
     * @return 返回结果
     */
    @Override
    public SmsLogInfoVO getDetail(Integer id) {
        SmsLog smsLog = getInfo(id);
        if (StringUtils.isNull(smsLog)) {
            return null;
        }
        // 实例化VO
        SmsLogInfoVO smsLogInfoVO = new SmsLogInfoVO();
        BeanUtils.copyProperties(smsLog, smsLogInfoVO);
        // 日志类型
        Integer type = smsLogInfoVO.getType();
        if (StringUtils.isNotNull(type)) {
            smsLogInfoVO.setTypeText(DictResolver.getDictItemName("sms_type", type.toString()));
        }
        // 返回结果
        return smsLogInfoVO;
    }

    /**
     * 添加短信记录
     *
     * @param smsLogAddDTO 参数
     * @return 返回结果
     */
    @Override
    public R add(SmsLogAddDTO smsLogAddDTO) {
        // 实例化对象
        SmsLog smsLog = new SmsLog();
        // 属性拷贝
        BeanUtils.copyProperties(smsLogAddDTO, smsLog);
        // 发送时间
        smsLog.setSendTime(LocalDateTime.now());
        boolean result = save(smsLog);
        if (!result) {
            return R.failed();
        }
        return R.ok();
    }

    /**
     * 更新短信记录
     *
     * @param smsLogId 短信日志ID
     * @return 返回结果
     */
    @Override
    public R read(Integer smsLogId) {
        // 根据ID查询信息
        SmsLog smsLog = getInfo(smsLogId);
        if (StringUtils.isNull(smsLog)) {
            return R.failed("记录不存在");
        }
        // 设置已读状态
        smsLog.setStatus(1);
        // 设置已读时间
        smsLog.setReadTime(LocalDateTime.now());
        boolean result = updateById(smsLog);
        if (!result) {
            return R.failed();
        }
        return R.ok();
    }

    /**
     * 删除短信记录
     *
     * @param idList 短信记录ID
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

}
