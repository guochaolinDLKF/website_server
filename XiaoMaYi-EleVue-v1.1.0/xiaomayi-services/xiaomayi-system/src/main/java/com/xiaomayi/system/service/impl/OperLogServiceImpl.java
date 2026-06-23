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
import com.xiaomayi.system.dto.operlog.OperLogPageDTO;
import com.xiaomayi.system.entity.OperLog;
import com.xiaomayi.system.mapper.OperLogMapper;
import com.xiaomayi.system.service.OperLogService;
import com.xiaomayi.system.utils.DictResolver;
import com.xiaomayi.system.vo.operlog.OperLogInfoVO;
import com.xiaomayi.system.vo.operlog.OperLogListVO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 操作日志 服务实现类
 * </p>
 *
 * @author 小蚂蚁云团队
 * @since 2024-05-27
 */
@Service
public class OperLogServiceImpl extends ServiceImpl<OperLogMapper, OperLog> implements OperLogService {

    /**
     * 查询分页列表
     *
     * @param operLogPageDTO 查询条件
     * @return 返回结果
     */
    @Override
    public Page<OperLog> page(OperLogPageDTO operLogPageDTO) {
        // 分页设置
        Page<OperLog> page = new Page<>(operLogPageDTO.getPageNo(), operLogPageDTO.getPageSize());
        // 查询条件
        LambdaQueryWrapper<OperLog> wrapper = new LambdaQueryWrapper<OperLog>()
                // 请求标题
                .like(StringUtils.isNotEmpty(operLogPageDTO.getTitle()), OperLog::getTitle, operLogPageDTO.getTitle())
                // 请求类型
                .eq(StringUtils.isNotNull(operLogPageDTO.getType()) && operLogPageDTO.getType() > 0, OperLog::getType, operLogPageDTO.getType())
                // 请求状态：0-正常 1-异常
                .eq(StringUtils.isNotNull(operLogPageDTO.getStatus()) && operLogPageDTO.getStatus() > 0, OperLog::getStatus, operLogPageDTO.getStatus())
                .eq(OperLog::getDelFlag, 0)
                .orderByDesc(OperLog::getId);
        // 查询分页数据
        Page<OperLog> pageData = page(page, wrapper);
        pageData.convert(item -> {
            // 实例化VO对象
            OperLogListVO operLogListVO = new OperLogListVO();
            BeanUtils.copyProperties(item, operLogListVO);
            // 日志类型
            Integer type = operLogListVO.getType();
            if (StringUtils.isNotNull(type)) {
                operLogListVO.setTypeText(DictResolver.getDictItemName("logger_type", type.toString()));
            }
            // 日志来源
            Integer source = operLogListVO.getSource();
            if (StringUtils.isNotNull(source)) {
                operLogListVO.setSourceText(DictResolver.getDictItemName("logger_source", source.toString()));
            }
            return operLogListVO;
        });
        // 返回结果
        return pageData;
    }

    /**
     * 根据ID查询信息
     *
     * @param id 操作日志ID
     * @return 返回结果
     */
    @Override
    public OperLog getInfo(Integer id) {
        OperLog operLog = getById(id);
        if (StringUtils.isNull(operLog) || !operLog.getDelFlag().equals(0)) {
            return null;
        }
        return operLog;
    }

    /**
     * 根据ID查询详情
     *
     * @param id 操作日志ID
     * @return 返回结果
     */
    @Override
    public OperLogInfoVO getDetail(Integer id) {
        OperLog operLog = getInfo(id);
        if (StringUtils.isNull(operLog)) {
            return null;
        }
        // 实例化VO
        OperLogInfoVO operLogInfoVO = new OperLogInfoVO();
        BeanUtils.copyProperties(operLog, operLogInfoVO);
        // 日志类型
        Integer type = operLog.getType();
        if (StringUtils.isNotNull(type)) {
            operLogInfoVO.setTypeText(DictResolver.getDictItemName("logger_type", type.toString()));
        }
        // 日志来源
        Integer source = operLog.getSource();
        if (StringUtils.isNotNull(source)) {
            operLogInfoVO.setSourceText(DictResolver.getDictItemName("logger_source", source.toString()));
        }
        return operLogInfoVO;
    }

    /**
     * 删除操作日志
     *
     * @param idList 操作日志ID
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
