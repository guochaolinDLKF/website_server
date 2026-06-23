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
import com.xiaomayi.quartz.dto.job.JobAddDTO;
import com.xiaomayi.quartz.dto.job.JobPageDTO;
import com.xiaomayi.quartz.dto.job.JobStatusDTO;
import com.xiaomayi.quartz.dto.job.JobUpdateDTO;
import com.xiaomayi.quartz.entity.Job;
import com.xiaomayi.quartz.mapper.JobMapper;
import com.xiaomayi.quartz.service.JobService;
import com.xiaomayi.quartz.vo.job.JobInfoVO;
import com.xiaomayi.quartz.vo.job.JobListVO;
import com.xiaomayi.scheduler.model.ScheduleJob;
import com.xiaomayi.scheduler.utils.ScheduleUtils;
import com.xiaomayi.system.utils.DictResolver;
import lombok.AllArgsConstructor;
import org.quartz.Scheduler;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 定时任务调度 服务实现类
 * </p>
 *
 * @author 小蚂蚁云团队
 * @since 2024-06-13
 */
@Service
@AllArgsConstructor
public class JobServiceImpl extends ServiceImpl<JobMapper, Job> implements JobService {

    private final Scheduler scheduler;

    /**
     * 初始化任务调度
     */
    @Override
    public void initScheduleJob() {
        // 获取运行中的任务列表
        List<Job> jobList = getJobList(1);
        // 查询结果判空
        if (StringUtils.isEmpty(jobList)) {
            return;
        }
        // 遍历定时任务
        for (Job job : jobList) {
            // 实例化任务调度任务
            ScheduleJob scheduleJob = new ScheduleJob();
            BeanUtils.copyProperties(job, scheduleJob);
            // 检查定时任务是否存在
            boolean jobExist = ScheduleUtils.checkExists(scheduler, job.getJobName(), job.getJobGroup());
            // 创建定时调度任务
            if (!jobExist) {
                // 不存在，则创建一个新的任务
                ScheduleUtils.createScheduleJob(scheduler, scheduleJob);
            } else {
                // 已存在，直接更新相应的定时设置
                ScheduleUtils.updateScheduleJob(scheduler, scheduleJob);
            }
        }
    }

    /**
     * 查询分页列表
     *
     * @param jobPageDTO 查询条件
     * @return 返回结果
     */
    @Override
    public Page<Job> page(JobPageDTO jobPageDTO) {
        // 分页设置
        Page<Job> page = new Page<>(jobPageDTO.getPageNo(), jobPageDTO.getPageSize());
        // 查询条件
        LambdaQueryWrapper<Job> wrapper = new LambdaQueryWrapper<Job>()
                // 任务名称
                .like(StringUtils.isNotEmpty(jobPageDTO.getJobName()), Job::getJobName, jobPageDTO.getJobName())
                // 任务状态：0-未发布 1-运行中 2-暂停
                .eq(StringUtils.isNotNull(jobPageDTO.getStatus()) && jobPageDTO.getStatus() > 0, Job::getStatus, jobPageDTO.getStatus())
                .eq(Job::getDelFlag, 0)
                .orderByAsc(Job::getId);
        // 查询分页数据
        Page<Job> pageData = page(page, wrapper);
        // 遍历数据源
        pageData.convert(item -> {
            // 实例化VO对象
            JobListVO jobListVO = new JobListVO();
            BeanUtils.copyProperties(item, jobListVO);
            // 执行策略
            Integer executePolicy = jobListVO.getExecutePolicy();
            if (StringUtils.isNotNull(executePolicy)) {
                jobListVO.setExecutePolicyText(DictResolver.getDictItemName("job_execute_policy", executePolicy.toString()));
            }
            return jobListVO;
        });
        // 返回结果
        return pageData;
    }

    /**
     * 获取任务列表
     *
     * @param status 任务状态：0-未发布 1-运行中 2-暂停
     * @return 返回结果
     */
    @Override
    public List<Job> getJobList(Integer status) {
        List<Job> jobList = list(new LambdaQueryWrapper<Job>()
                // 任务状态
                .eq(StringUtils.isNotNull(status), Job::getStatus, status)
                .eq(Job::getDelFlag, 0));
        return jobList;
    }

    /**
     * 根据ID查询信息
     *
     * @param id 定时任务调度ID
     * @return 返回结果
     */
    @Override
    public Job getInfo(Integer id) {
        Job job = getById(id);
        if (StringUtils.isNull(job) || !job.getDelFlag().equals(0)) {
            return null;
        }
        return job;
    }

    /**
     * 根据ID查询详情
     *
     * @param id 定时任务调度ID
     * @return 返回结果
     */
    @Override
    public JobInfoVO getDetail(Integer id) {
        Job job = getInfo(id);
        if (StringUtils.isNull(job)) {
            return null;
        }
        // 实例化VO
        JobInfoVO jobInfoVO = new JobInfoVO();
        BeanUtils.copyProperties(job, jobInfoVO);
        // 执行策略
        Integer executePolicy = jobInfoVO.getExecutePolicy();
        if (StringUtils.isNotNull(executePolicy)) {
            jobInfoVO.setExecutePolicyText(DictResolver.getDictItemName("job_execute_policy", executePolicy.toString()));
        }
        return jobInfoVO;
    }

    /**
     * 添加定时任务调度
     *
     * @param jobAddDTO 参数
     * @return 返回结果
     */
    @Override
    public R add(JobAddDTO jobAddDTO) {
        // 检查任务触发器是否已存在
        if (checkExist(jobAddDTO.getJobTrigger(), 0)) {
            return R.failed("任务触发器已存在");
        }
        // 实例化对象
        Job job = new Job();
        // 属性拷贝
        BeanUtils.copyProperties(jobAddDTO, job);
        // 设置任务状态：未发布
        job.setStatus(0);
        boolean result = save(job);
        if (!result) {
            return R.failed();
        }
        // 创建一个定时调度任务
        ScheduleJob scheduleJob = new ScheduleJob();
        BeanUtils.copyProperties(job, scheduleJob);
        ScheduleUtils.createScheduleJob(scheduler, scheduleJob);
        // 返回结果
        return R.ok();
    }

    /**
     * 更新定时任务调度
     *
     * @param jobUpdateDTO 参数
     * @return 返回结果
     */
    @Override
    public R update(JobUpdateDTO jobUpdateDTO) {
        // 根据ID查询信息
        Job job = getInfo(jobUpdateDTO.getId());
        if (StringUtils.isNull(job)) {
            return R.failed("记录不存在");
        }
        // 检查任务触发器是否已存在
        if (checkExist(jobUpdateDTO.getJobTrigger(), job.getId())) {
            return R.failed("任务触发器已存在");
        }
        // 属性拷贝
        BeanUtils.copyProperties(jobUpdateDTO, job);
        boolean result = updateById(job);
        if (!result) {
            return R.failed();
        }

        // 更新任务调度
        ScheduleJob scheduleJob = new ScheduleJob();
        BeanUtils.copyProperties(job, scheduleJob);
        // 执行更新任务操作
        ScheduleUtils.updateScheduleJob(scheduler, scheduleJob);
        // 返回结果
        return R.ok();
    }

    /**
     * 删除定时任务调度
     *
     * @param id 任务ID
     * @return 返回结果
     */
    @Override
    public R delete(Integer id) {
        // 根据ID查询任务
        Job job = getInfo(id);
        if (StringUtils.isNull(job)) {
            return R.failed("任务不存在");
        }
        // 设置删除状态
        job.setStatus(3);
        // 设置删除标识
        job.setDelFlag(1);
        // 删除任务
        boolean result = updateById(job);
        if (!result) {
            return R.failed();
        }
        // 删除任务
        ScheduleJob scheduleJob = new ScheduleJob();
        BeanUtils.copyProperties(job, scheduleJob);
        // 执行删除运行的任务操作
        ScheduleUtils.deleteScheduleJob(scheduler, job.getId(), job.getJobGroup());
        // 返回结果
        return R.ok();
    }

    /**
     * 删除定时任务调度
     *
     * @param idList 定时任务调度ID
     * @return 返回结果
     */
    @Override
    public R batchDelete(List<Integer> idList) {
        // 删除ID判空
        if (StringUtils.isEmpty(idList)) {
            return R.failed("删除记录ID不存在");
        }
        // 查询任务列表
        List<Job> jobList = list(new LambdaQueryWrapper<Job>()
                .in(Job::getId, idList)
                .eq(Job::getDelFlag, 0));
        if (StringUtils.isEmpty(jobList)) {
            return R.failed("任务不存在");
        }
        if (idList.size() != jobList.size()) {
            return R.failed("任务数据不匹配");
        }
        // 批量删除
        boolean result = removeBatchByIds(jobList);
        if (!result) {
            return R.failed();
        }
        // 遍历任务列表
        for (Job job : jobList) {
            // 删除运行的任务
            ScheduleUtils.deleteScheduleJob(scheduler, job.getId(), job.getJobGroup());
        }
        // 返回结果
        return R.ok();
    }

    /**
     * 检查任务触发器是否已存在
     *
     * @param jobTrigger 任务触发器
     * @param id         任务ID
     * @return 返回结果
     */
    @Override
    public boolean checkExist(String jobTrigger, Integer id) {
        Job job = getOne(new LambdaQueryWrapper<Job>()
                .eq(Job::getJobTrigger, jobTrigger)
                .ne(id > 0, Job::getId, id)
                .eq(Job::getDelFlag, 0), false);
        return StringUtils.isNotNull(job);
    }

    /**
     * 设置任务状态
     *
     * @param jobStatusDTO 参数
     * @return 返回结果
     */
    @Override
    public R status(JobStatusDTO jobStatusDTO) {
        // 任务ID
        Integer jobId = jobStatusDTO.getId();
        // 任务状态
        Integer status = jobStatusDTO.getStatus();
        // 查询定时任务
        Job job = getInfo(jobId);
        if (StringUtils.isNull(job)) {
            return R.failed("定时任务不存在");
        }
        // 设置状态
        job.setStatus(status);
        // 更新数据
        boolean result = updateById(job);
        if (!result) {
            return R.failed();
        }
        // 实例化任务调度任务
        ScheduleJob scheduleJob = new ScheduleJob();
        BeanUtils.copyProperties(job, scheduleJob);
        if (status.equals(1)) {
            // 创建任务
            // 检查定时任务是否存在
            boolean jobExist = ScheduleUtils.checkExists(scheduler, job.getJobName(), job.getJobGroup());
            // 创建定时调度任务
            if (!jobExist) {
                // 不存在，则创建一个新的任务
                ScheduleUtils.createScheduleJob(scheduler, scheduleJob);
            } else {
                // 已存在，直接更新相应的定时设置
                ScheduleUtils.updateScheduleJob(scheduler, scheduleJob);
            }
        } else {
            // 删除任务
            ScheduleUtils.deleteScheduleJob(scheduler, job.getId(), job.getJobGroup());
        }
        // 返回结果
        return R.ok();
    }

    /**
     * 执行一次任务
     *
     * @param jobId 任务ID
     * @return 返回结果
     */
    @Override
    public R runOnce(Integer jobId) {
        // 根据任务ID查询
        Job job = getInfo(jobId);
        if (StringUtils.isNull(job)) {
            return R.failed("任务不存在");
        }
        // 执行一次任务
        ScheduleJob scheduleJob = new ScheduleJob();
        BeanUtils.copyProperties(job, scheduleJob);
        // 执行一次任务操作
        ScheduleUtils.runOnce(scheduler, scheduleJob.getId(), scheduleJob.getJobGroup());
        // 返回结果
        return R.ok();
    }

    /**
     * 暂停任务
     *
     * @param jobId 任务ID
     * @return 返回结果
     */
    @Override
    public R pause(Integer jobId) {
        // 根据ID查询任务
        Job job = getInfo(jobId);
        if (StringUtils.isNull(job)) {
            return R.failed("任务不存在");
        }
        // 判断任务状态
        if (job.getStatus().equals(2)) {
            return R.failed("任务已在暂停状态");
        }
        // 设置暂停状态
        job.setStatus(2);
        boolean result = updateById(job);
        if (!result) {
            return R.failed("暂停失败");
        }
        // 暂停任务
        ScheduleJob scheduleJob = new ScheduleJob();
        BeanUtils.copyProperties(job, scheduleJob);
        // 执行暂停任务操作
        ScheduleUtils.pauseJob(scheduler, scheduleJob.getId(), scheduleJob.getJobGroup());
        // 返回结果
        return R.ok();
    }

    /**
     * 恢复任务
     *
     * @param jobId 任务ID
     * @return 返回结果
     */
    @Override
    public R resume(Integer jobId) {
        // 根据ID查询任务
        Job job = getInfo(jobId);
        if (StringUtils.isNull(job)) {
            return R.failed("任务不存在");
        }
        // 判断任务状态
        if (job.getStatus().equals(1)) {
            return R.failed("任务已在运行中状态");
        }
        // 设置运行状态
        job.setStatus(1);
        boolean result = updateById(job);
        if (!result) {
            return R.failed("暂停失败");
        }
        // 恢复任务
        ScheduleJob scheduleJob = new ScheduleJob();
        BeanUtils.copyProperties(job, scheduleJob);
        // 执行恢复任务操作
        ScheduleUtils.resumeJob(scheduler, scheduleJob.getId(), scheduleJob.getJobGroup());
        // 返回结果
        return R.ok();
    }
}
