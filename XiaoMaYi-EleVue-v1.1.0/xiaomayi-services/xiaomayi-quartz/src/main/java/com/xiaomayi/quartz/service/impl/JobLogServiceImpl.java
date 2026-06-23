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

package com.xiaomayi.quartz.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiaomayi.core.utils.R;
import com.xiaomayi.core.utils.StringUtils;
import com.xiaomayi.quartz.dto.joblog.JobLogPageDTO;
import com.xiaomayi.quartz.entity.JobLog;
import com.xiaomayi.quartz.mapper.JobLogMapper;
import com.xiaomayi.quartz.service.JobLogService;
import com.xiaomayi.quartz.vo.joblog.JobLogInfoVO;
import com.xiaomayi.quartz.vo.joblog.JobLogListVO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 定时任务日志 服务实现类
 * </p>
 *
 * @author 小蚂蚁云团队
 * @since 2024-06-13
 */
@Service
public class JobLogServiceImpl extends ServiceImpl<JobLogMapper, JobLog> implements JobLogService {

    /**
     * 查询分页列表
     *
     * @param jobLogPageDTO 查询条件
     * @return 返回结果
     */
    @Override
    public Page<JobLog> page(JobLogPageDTO jobLogPageDTO) {
        // 分页设置
        Page<JobLog> page = new Page<>(jobLogPageDTO.getPageNo(), jobLogPageDTO.getPageSize());
        // 查询条件
        LambdaQueryWrapper<JobLog> wrapper = new LambdaQueryWrapper<JobLog>()
                // 任务ID
                .eq(JobLog::getJobId, jobLogPageDTO.getJobId())
                // 任务名称
                .like(StringUtils.isNotEmpty(jobLogPageDTO.getJobName()), JobLog::getJobName, jobLogPageDTO.getJobName())
                // 执行状态：0-正常 1-失败
                .eq(StringUtils.isNotNull(jobLogPageDTO.getStatus()) && jobLogPageDTO.getStatus() > 0, JobLog::getStatus, jobLogPageDTO.getStatus())
                .eq(JobLog::getDelFlag, 0)
                .orderByDesc(JobLog::getId);
        // 查询分页数据
        Page<JobLog> pageData = page(page, wrapper);
        // 遍历数据源
        pageData.convert(item -> {
            // 实例化VO对象
            JobLogListVO jobLogListVO = new JobLogListVO();
            BeanUtils.copyProperties(item, jobLogListVO);
            return jobLogListVO;
        });
        // 返回结果
        return pageData;
    }

    /**
     * 根据ID查询信息
     *
     * @param id 定时任务日志ID
     * @return 返回结果
     */
    @Override
    public JobLog getInfo(Integer id) {
        JobLog jobLog = getById(id);
        if (StringUtils.isNull(jobLog) || !jobLog.getDelFlag().equals(0)) {
            return null;
        }
        return jobLog;
    }

    /**
     * 根据ID查询详情
     *
     * @param id 定时任务日志ID
     * @return 返回结果
     */
    @Override
    public JobLogInfoVO getDetail(Integer id) {
        JobLog jobLog = getInfo(id);
        if (StringUtils.isNull(jobLog)) {
            return null;
        }
        // 实例化VO
        JobLogInfoVO jobLogInfoVO = new JobLogInfoVO();
        BeanUtils.copyProperties(jobLog, jobLogInfoVO);
        return jobLogInfoVO;
    }

    /**
     * 删除定时任务日志
     *
     * @param idList 定时任务日志ID
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
