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

package com.xiaomayi.cms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiaomayi.cms.dto.ad.AdAddDTO;
import com.xiaomayi.cms.dto.ad.AdListDTO;
import com.xiaomayi.cms.dto.ad.AdPageDTO;
import com.xiaomayi.cms.dto.ad.AdUpdateDTO;
import com.xiaomayi.cms.entity.Ad;
import com.xiaomayi.cms.mapper.AdMapper;
import com.xiaomayi.cms.service.AdService;
import com.xiaomayi.cms.vo.ad.AdInfoVO;
import com.xiaomayi.cms.vo.ad.AdListVO;
import com.xiaomayi.core.config.AppConfig;
import com.xiaomayi.core.utils.CommonUtils;
import com.xiaomayi.core.utils.R;
import com.xiaomayi.core.utils.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 广告 服务实现类
 * </p>
 *
 * @author 小蚂蚁云团队
 * @since 2024-07-03
 */
@Service
public class AdServiceImpl extends ServiceImpl<AdMapper, Ad> implements AdService {

    /**
     * 查询分页列表
     *
     * @param adPageDTO 查询条件
     * @return 返回结果
     */
    @Override
    public Page<Ad> page(AdPageDTO adPageDTO) {
        // 分页设置
        Page<Ad> page = new Page<>(adPageDTO.getPageNo(), adPageDTO.getPageSize());
        // 查询条件
        LambdaQueryWrapper<Ad> wrapper = new LambdaQueryWrapper<Ad>()
                // 广告标题
                .like(StringUtils.isNotEmpty(adPageDTO.getTitle()), Ad::getTitle, adPageDTO.getTitle())
                // 广告状态：1-在用 2-停用
                .eq(StringUtils.isNotNull(adPageDTO.getStatus()) && adPageDTO.getStatus() > 0, Ad::getStatus, adPageDTO.getStatus())
                // 广告位ID
                .eq(StringUtils.isNotNull(adPageDTO.getAdSortId()) && adPageDTO.getAdSortId() > 0, Ad::getAdSortId, adPageDTO.getAdSortId())
                .eq(Ad::getDelFlag, 0)
                .orderByDesc(Ad::getId);
        // 查询分页数据
        Page<Ad> pageData = page(page, wrapper);
        // 遍历数据源
        pageData.convert(item -> {
            // 实例化VO对象
            AdListVO adListVO = new AdListVO();
            BeanUtils.copyProperties(item, adListVO);
            // 广告封面
            if (StringUtils.isNotEmpty(adListVO.getCover())) {
                adListVO.setCover(CommonUtils.getFileURL(adListVO.getCover()));
            }
            return adListVO;
        });
        // 返回结果
        return pageData;
    }

    /**
     * 查询数据列表
     *
     * @param adListDTO 查询条件
     * @return 返回结果
     */
    @Override
    public List<Ad> getList(AdListDTO adListDTO) {
        List<Ad> adList = list(new LambdaQueryWrapper<Ad>()
                .eq(Ad::getDelFlag, 0)
                .orderByAsc(Ad::getId));
        return adList;
    }

    /**
     * 根据ID查询信息
     *
     * @param id 广告ID
     * @return 返回结果
     */
    @Override
    public Ad getInfo(Integer id) {
        Ad ad = getById(id);
        if (StringUtils.isNull(ad) || !ad.getDelFlag().equals(0)) {
            return null;
        }
        // 广告封面
        if (StringUtils.isNotEmpty(ad.getCover())) {
            ad.setCover(CommonUtils.getFileURL(ad.getCover()));
        }
        return ad;
    }

    /**
     * 根据ID查询详情
     *
     * @param id 广告ID
     * @return 返回结果
     */
    @Override
    public AdInfoVO getDetail(Integer id) {
        Ad ad = getInfo(id);
        if (StringUtils.isNull(ad)) {
            return null;
        }
        // 实例化VO
        AdInfoVO adInfoVO = new AdInfoVO();
        BeanUtils.copyProperties(ad, adInfoVO);

        // 富文本处理
        if (StringUtils.isNotEmpty(adInfoVO.getContent())) {
            adInfoVO.setContent(CommonUtils.getContent(adInfoVO.getContent()));
        }

        // 返回结果
        return adInfoVO;
    }

    /**
     * 添加广告
     *
     * @param adAddDTO 参数
     * @return 返回结果
     */
    @Override
    public R add(AdAddDTO adAddDTO) {
        // 实例化对象
        Ad ad = new Ad();
        // 属性拷贝
        BeanUtils.copyProperties(adAddDTO, ad);
        // 封面处理
        if (StringUtils.isNotEmpty(ad.getCover()) && ad.getCover().contains(AppConfig.getDomain())) {
            ad.setCover(ad.getCover().replaceAll(AppConfig.getDomain(), ""));
        }
        // 富文本处理
        if (StringUtils.isNotEmpty(ad.getContent())) {
            ad.setContent(ad.getContent().replaceAll(AppConfig.getDomain(), ""));
        }
        boolean result = save(ad);
        if (!result) {
            return R.failed();
        }
        return R.ok();
    }

    /**
     * 更新广告
     *
     * @param adUpdateDTO 参数
     * @return 返回结果
     */
    @Override
    public R update(AdUpdateDTO adUpdateDTO) {
        // 根据ID查询信息
        Ad ad = getInfo(adUpdateDTO.getId());
        if (StringUtils.isNull(ad)) {
            return R.failed("记录不存在");
        }
        // 属性拷贝
        BeanUtils.copyProperties(adUpdateDTO, ad);
        // 封面处理
        if (StringUtils.isNotEmpty(ad.getCover()) && ad.getCover().contains(AppConfig.getDomain())) {
            ad.setCover(ad.getCover().replaceAll(AppConfig.getDomain(), ""));
        }
        // 富文本处理
        if (StringUtils.isNotEmpty(ad.getContent())) {
            ad.setContent(ad.getContent().replaceAll(AppConfig.getDomain(), ""));
        }
        boolean result = updateById(ad);
        if (!result) {
            return R.failed();
        }
        return R.ok();
    }

    /**
     * 删除广告
     *
     * @param id 广告ID
     * @return 返回结果
     */
    @Override
    public R delete(Integer id) {
        // 根据ID查询信息
        Ad ad = getInfo(id);
        if (StringUtils.isNull(ad)) {
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
     * 批量删除广告
     *
     * @param idList 广告ID
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
