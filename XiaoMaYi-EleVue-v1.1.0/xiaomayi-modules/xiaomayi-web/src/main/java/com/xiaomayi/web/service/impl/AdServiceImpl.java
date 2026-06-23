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

package com.xiaomayi.web.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiaomayi.core.utils.CommonUtils;
import com.xiaomayi.core.utils.StringUtils;
import com.xiaomayi.web.entity.Ad;
import com.xiaomayi.web.mapper.AdMapper;
import com.xiaomayi.web.service.AdService;
import com.xiaomayi.web.vo.ad.AdListVO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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
     * 获取广告列表
     *
     * @param tenantId 租户ID
     * @param adSortId 广告位ID
     * @param type     广告格式：1-图片 2-文字 3-视频 4-推荐
     * @param limit    数据条数
     * @return 返回结果
     */
    @Override
    public List<AdListVO> getAdList(Integer tenantId, Integer adSortId, Integer type, Integer limit) {
        // 获取广告推荐列表
        List<Ad> adList = list(new LambdaQueryWrapper<Ad>()
                .eq(Ad::getTenantId, tenantId)
                .eq(Ad::getAdSortId, adSortId)
                .eq(Ad::getType, type)
                .eq(Ad::getDelFlag, 0)
                .last("limit " + limit));

        // 实例化广告VO列表
        List<AdListVO> adListVOList = new ArrayList<AdListVO>();
        // 遍历数据源
        if (StringUtils.isNotEmpty(adList)) {
            for (Ad ad : adList) {
                AdListVO adListVO = new AdListVO();
                BeanUtils.copyProperties(ad, adListVO);
                // 广告封面
                if (StringUtils.isNotEmpty(adListVO.getCover())) {
                    adListVO.setCover(CommonUtils.getFileURL(adListVO.getCover()));
                }
                // 加入列表
                adListVOList.add(adListVO);
            }
        }

        // 返回结果
        return adListVOList;
    }
}
