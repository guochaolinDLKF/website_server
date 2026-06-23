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

package com.xiaomayi.quartz.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xiaomayi.core.utils.R;
import com.xiaomayi.quartz.dto.job.JobAddDTO;
import com.xiaomayi.quartz.dto.job.JobPageDTO;
import com.xiaomayi.quartz.dto.job.JobStatusDTO;
import com.xiaomayi.quartz.dto.job.JobUpdateDTO;
import com.xiaomayi.quartz.entity.Job;
import com.xiaomayi.quartz.vo.job.JobInfoVO;

import java.util.List;

/**
 * <p>
 * 定时任务调度 服务类
 * </p>
 *
 * @author 小蚂蚁云团队
 * @since 2024-06-13
 */
public interface JobService extends IService<Job> {

    /**
     * 初始化任务调度
     */
    void initScheduleJob();

    /**
     * 查询分页列表
     *
     * @param jobPageDTO 查询条件
     * @return 返回结果
     */
    Page<Job> page(JobPageDTO jobPageDTO);

    /**
     * 获取任务列表
     *
     * @param status 任务状态：0-未发布 1-运行中 2-暂停
     * @return 返回结果
     */
    List<Job> getJobList(Integer status);

    /**
     * 根据ID查询信息
     *
     * @param id 定时任务调度ID
     * @return 返回结果
     */
    Job getInfo(Integer id);

    /**
     * 根据ID查询详情
     *
     * @param id 定时任务调度ID
     * @return 返回结果
     */
    JobInfoVO getDetail(Integer id);

    /**
     * 添加定时任务调度
     *
     * @param jobAddDTO 参数
     * @return 返回结果
     */
    R add(JobAddDTO jobAddDTO);

    /**
     * 更新定时任务调度
     *
     * @param jobUpdateDTO 参数
     * @return 返回结果
     */
    R update(JobUpdateDTO jobUpdateDTO);

    /**
     * 删除定时任务调度
     *
     * @param id 任务ID
     * @return 返回结果
     */
    R delete(Integer id);

    /**
     * 批量删除定时任务调度
     *
     * @param idList 定时任务调度ID
     * @return 返回结果
     */
    R batchDelete(List<Integer> idList);

    /**
     * 检查任务触发器是否已存在
     *
     * @param jobTrigger 任务触发器
     * @param id         任务ID
     * @return 返回结果
     */
    boolean checkExist(String jobTrigger, Integer id);

    /**
     * 设置任务状态
     *
     * @param jobStatusDTO 参数
     * @return 返回结果
     */
    R status(JobStatusDTO jobStatusDTO);

    /**
     * 执行一次任务
     *
     * @param jobId 任务ID
     * @return 返回结果
     */
    R runOnce(Integer jobId);

    /**
     * 暂停任务
     *
     * @param jobId 任务ID
     * @return 返回结果
     */
    R pause(Integer jobId);

    /**
     * 恢复任务
     *
     * @param jobId 任务ID
     * @return 返回结果
     */
    R resume(Integer jobId);
}
