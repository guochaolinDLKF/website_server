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
import com.xiaomayi.core.config.AppConfig;
import com.xiaomayi.core.utils.CommonUtils;
import com.xiaomayi.core.utils.R;
import com.xiaomayi.core.utils.StringUtils;
import com.xiaomayi.system.dto.notice.NoticeAddDTO;
import com.xiaomayi.system.dto.notice.NoticePageDTO;
import com.xiaomayi.system.dto.notice.NoticeUpdateDTO;
import com.xiaomayi.system.entity.Notice;
import com.xiaomayi.system.mapper.NoticeMapper;
import com.xiaomayi.system.service.NoticeService;
import com.xiaomayi.system.vo.notice.NoticeInfoVO;
import com.xiaomayi.system.vo.notice.NoticeListVO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 通知公告 服务实现类
 * </p>
 *
 * @author 小蚂蚁云团队
 * @since 2024-03-23
 */
@Service
public class NoticeServiceImpl extends ServiceImpl<NoticeMapper, Notice> implements NoticeService {

    /**
     * 查询分页列表
     *
     * @param noticePageDTO 查询条件
     * @return 返回结果
     */
    @Override
    public Page<Notice> page(NoticePageDTO noticePageDTO) {
        // 分页设置
        Page<Notice> page = new Page<>(noticePageDTO.getPageNo(), noticePageDTO.getPageSize());
        // 查询条件
        LambdaQueryWrapper<Notice> wrapper = new LambdaQueryWrapper<Notice>()
                // 通知标题
                .like(StringUtils.isNotEmpty(noticePageDTO.getTitle()), Notice::getTitle, noticePageDTO.getTitle())
                // 通知类型：1-通知 2-公告
                .eq(StringUtils.isNotNull(noticePageDTO.getType()) && noticePageDTO.getType() > 0, Notice::getType, noticePageDTO.getType())
                // 通知状态：1-正常 2-关闭
                .eq(StringUtils.isNotNull(noticePageDTO.getStatus()) && noticePageDTO.getStatus() > 0, Notice::getStatus, noticePageDTO.getStatus())
                .eq(Notice::getDelFlag, 0)
                .orderByAsc(Notice::getId);
        // 查询分页数据
        Page<Notice> pageData = page(page, wrapper);
        // 遍历数据源
        pageData.convert(item -> {
            // 实例化VO对象
            NoticeListVO noticeListVO = new NoticeListVO();
            BeanUtils.copyProperties(item, noticeListVO);
            // 通知封面
            String cover = noticeListVO.getCover();
            if (StringUtils.isNotEmpty(cover)) {
                noticeListVO.setCover(CommonUtils.getFileURL(cover));
            }
            return noticeListVO;
        });
        // 返回结果
        return pageData;
    }

    /**
     * 根据ID查询信息
     *
     * @param id 通知公告ID
     * @return 返回结果
     */
    @Override
    public Notice getInfo(Integer id) {
        Notice notice = getById(id);
        if (StringUtils.isNull(notice) || !notice.getDelFlag().equals(0)) {
            return null;
        }
        return notice;
    }

    /**
     * 根据ID查询详情
     *
     * @param id 通知公告ID
     * @return 返回结果
     */
    @Override
    public NoticeInfoVO getDetail(Integer id) {
        Notice notice = getInfo(id);
        if (StringUtils.isNull(notice)) {
            return null;
        }
        // 实例化VO
        NoticeInfoVO noticeInfoVO = new NoticeInfoVO();
        BeanUtils.copyProperties(notice, noticeInfoVO);

        // 通知封面
        String cover = noticeInfoVO.getCover();
        if (StringUtils.isNotEmpty(cover)) {
            noticeInfoVO.setCover(CommonUtils.getFileURL(cover));
        }

        // 富文本处理
        String content = notice.getContent();
        if (StringUtils.isNotEmpty(content)) {
            noticeInfoVO.setContent(CommonUtils.getContent(content));
        }

        // 返回结果
        return noticeInfoVO;
    }

    /**
     * 添加通知公告
     *
     * @param noticeAddDTO 参数
     * @return 返回结果
     */
    @Override
    public R add(NoticeAddDTO noticeAddDTO) {
        // 实例化对象
        Notice notice = new Notice();
        // 属性拷贝
        BeanUtils.copyProperties(noticeAddDTO, notice);
        // 通知封面
        String cover = noticeAddDTO.getCover();
        if (StringUtils.isNotEmpty(cover) && cover.contains(AppConfig.getDomain())) {
            notice.setCover(cover.replaceAll(AppConfig.getDomain(), ""));
        }
        // 富文本处理
        String content = noticeAddDTO.getContent();
        if (StringUtils.isNotEmpty(content)) {
            notice.setContent(content.replaceAll(AppConfig.getDomain(), ""));
        }
        boolean result = save(notice);
        if (!result) {
            return R.failed();
        }
        return R.ok();
    }

    /**
     * 更新通知公告
     *
     * @param noticeUpdateDTO 参数
     * @return 返回结果
     */
    @Override
    public R update(NoticeUpdateDTO noticeUpdateDTO) {
        // 根据ID查询信息
        Notice notice = getInfo(noticeUpdateDTO.getId());
        if (StringUtils.isNull(notice)) {
            return R.failed("记录不存在");
        }
        // 属性拷贝
        BeanUtils.copyProperties(noticeUpdateDTO, notice);
        // 通知封面
        String cover = noticeUpdateDTO.getCover();
        if (StringUtils.isNotEmpty(cover) && cover.contains(AppConfig.getDomain())) {
            notice.setCover(cover.replaceAll(AppConfig.getDomain(), ""));
        }
        // 富文本处理
        String content = noticeUpdateDTO.getContent();
        if (StringUtils.isNotEmpty(content)) {
            notice.setContent(content.replaceAll(AppConfig.getDomain(), ""));
        }
        boolean result = updateById(notice);
        if (!result) {
            return R.failed();
        }
        return R.ok();
    }

    /**
     * 删除通知公告
     *
     * @param idList 通知公告ID
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
