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
import com.xiaomayi.system.dto.emaillog.EmailLogPageDTO;
import com.xiaomayi.system.entity.EmailLog;
import com.xiaomayi.system.mapper.EmailLogMapper;
import com.xiaomayi.system.service.EmailLogService;
import com.xiaomayi.system.vo.emaillog.EmailLogInfoVO;
import com.xiaomayi.system.vo.emaillog.EmailLogListVO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 邮件日志 服务实现类
 * </p>
 *
 * @author 小蚂蚁云团队
 * @since 2024-06-16
 */
@Service
public class EmailLogServiceImpl extends ServiceImpl<EmailLogMapper, EmailLog> implements EmailLogService {

    /**
     * 查询分页列表
     *
     * @param emailLogPageDTO 查询条件
     * @return 返回结果
     */
    @Override
    public Page<EmailLog> page(EmailLogPageDTO emailLogPageDTO) {
        // 分页设置
        Page<EmailLog> page = new Page<>(emailLogPageDTO.getPageNo(), emailLogPageDTO.getPageSize());
        // 查询条件
        LambdaQueryWrapper<EmailLog> wrapper = new LambdaQueryWrapper<EmailLog>()
                // 邮件标题
                .like(StringUtils.isNotEmpty(emailLogPageDTO.getTitle()), EmailLog::getTitle, emailLogPageDTO.getTitle())
                // 邮件类型：1-登录 2-注册 3-找回密码 4-业务 5-其他
                .eq(StringUtils.isNotNull(emailLogPageDTO.getType()) && emailLogPageDTO.getType() > 0, EmailLog::getType, emailLogPageDTO.getType())
                // 业务类型：1-订单 2-其他
                .eq(StringUtils.isNotNull(emailLogPageDTO.getBizType()) && emailLogPageDTO.getBizType() > 0, EmailLog::getBizType, emailLogPageDTO.getBizType())
                // 状态：0-成功 1-失败
                .eq(StringUtils.isNotNull(emailLogPageDTO.getStatus()) && emailLogPageDTO.getStatus() > 0, EmailLog::getStatus, emailLogPageDTO.getStatus())
                .eq(EmailLog::getDelFlag, 0)
                .orderByDesc(EmailLog::getId);
        // 查询分页数据
        Page<EmailLog> pageData = page(page, wrapper);
        // 遍历数据源
        pageData.convert(item -> {
            // 实例化VO对象
            EmailLogListVO emailLogListVO = new EmailLogListVO();
            BeanUtils.copyProperties(item, emailLogListVO);
            return emailLogListVO;
        });
        // 返回结果
        return pageData;
    }

    /**
     * 根据ID查询信息
     *
     * @param id 邮件日志ID
     * @return 返回结果
     */
    @Override
    public EmailLog getInfo(Integer id) {
        EmailLog emailLog = getById(id);
        if (StringUtils.isNull(emailLog) || !emailLog.getDelFlag().equals(0)) {
            return null;
        }
        return emailLog;
    }

    /**
     * 根据ID查询详情
     *
     * @param id 邮件日志ID
     * @return 返回结果
     */
    @Override
    public EmailLogInfoVO getDetail(Integer id) {
        EmailLog emailLog = getInfo(id);
        if (StringUtils.isNull(emailLog)) {
            return null;
        }
        // 实例化VO
        EmailLogInfoVO emailLogInfoVO = new EmailLogInfoVO();
        BeanUtils.copyProperties(emailLog, emailLogInfoVO);
        return emailLogInfoVO;
    }

    /**
     * 删除邮件日志
     *
     * @param id 邮件日志ID
     * @return 返回结果
     */
    @Override
    public R delete(Integer id) {
        // 根据ID查询信息
        EmailLog emailLog = getInfo(id);
        if (StringUtils.isNull(emailLog)) {
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
     * 批量删除邮件日志
     *
     * @param idList 邮件日志ID
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

}
