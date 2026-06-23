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
import com.xiaomayi.web.entity.Article;
import com.xiaomayi.web.entity.LayoutItem;
import com.xiaomayi.web.mapper.LayoutItemMapper;
import com.xiaomayi.web.service.ArticleService;
import com.xiaomayi.web.service.LayoutItemService;
import com.xiaomayi.web.vo.layoutitem.LayoutItemListVO;
import lombok.AllArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


/**
 * <p>
 * 页面推荐 服务实现类
 * </p>
 *
 * @author 小蚂蚁云团队
 * @since 2024-07-03
 */
@Service
@AllArgsConstructor
public class LayoutItemServiceImpl extends ServiceImpl<LayoutItemMapper, LayoutItem> implements LayoutItemService {

    private final ArticleService articleService;

    /**
     * 获取布局推荐列表
     *
     * @param tenantId 租户ID
     * @param layoutId 布局ID
     * @param type     页面类型：1-CMS文章 2-通知公告
     * @param location 位置编号
     * @param limit    数据条数
     * @return 返回结果
     */
    @Override
    public List<LayoutItemListVO> getLayoutItemList(Integer tenantId, Integer layoutId, Integer location, Integer type, Integer limit) {
        // 查询布局推荐列表
        List<LayoutItem> layoutItems = list(new LambdaQueryWrapper<LayoutItem>()
                .eq(LayoutItem::getTenantId, tenantId)
                .eq(LayoutItem::getLayoutId, layoutId)
                .eq(LayoutItem::getLocation, location)
                .eq(LayoutItem::getType, type)
                .eq(LayoutItem::getDelFlag, 0)
                .orderByAsc(LayoutItem::getSort)
                .last("limit " + limit));

        // 实例化布局推荐VO列表
        List<LayoutItemListVO> layoutItemListVOS = new ArrayList<>();
        // 遍历数据源
        if (StringUtils.isNotEmpty(layoutItems)) {
            for (LayoutItem layoutItem : layoutItems) {
                LayoutItemListVO layoutItemListVO = new LayoutItemListVO();
                BeanUtils.copyProperties(layoutItem, layoutItemListVO);

                // 推荐图片处理
                if (StringUtils.isNotEmpty(layoutItemListVO.getImage())) {
                    layoutItemListVO.setImage(CommonUtils.getFileURL(layoutItemListVO.getImage()));
                }

                // 根据类型查询业务
                if (layoutItemListVO.getType().equals(1)) {
                    // 文章
                    Article article = articleService.getInfo(layoutItemListVO.getTypeId());
                    if (StringUtils.isNotNull(article)) {
                        // 文章标题
                        layoutItemListVO.setTypeText(article.getTitle());
                        // 文章导读
                        layoutItemListVO.setTypeIntro(article.getIntro());
                        // 文章封面
                        if (StringUtils.isEmpty(layoutItemListVO.getImage())) {
                            layoutItemListVO.setImage(article.getCover());
                        }
                    }
                }
                // 加入列表
                layoutItemListVOS.add(layoutItemListVO);
            }
        }
        // 返回结果
        return layoutItemListVOS;
    }

//    /**
//     * 获取推荐信息
//     *
//     * @param tenantId 租户ID
//     * @param layoutId 布局ID
//     * @param location 位置编号
//     * @param type     页面类型：1-CMS文章 2-通知公告
//     * @return 返回结果
//     */
//    @Override
//    public LayoutItemInfoVO getLayoutItemInfo(Integer tenantId, Integer layoutId, Integer location, Integer type) {
//        // 获取推荐信息
//        LayoutItem layoutItem = getOne(new LambdaQueryWrapper<LayoutItem>()
//                .eq(LayoutItem::getTenantId, tenantId)
//                .eq(LayoutItem::getLayoutId, layoutId)
//                .eq(LayoutItem::getLocation, location)
//                .eq(LayoutItem::getType, type)
//                .orderByAsc(LayoutItem::getSort)
//                .last("limit 1"));
//        // 查询结果判空
//        if (StringUtils.isNull(layoutItem)) {
//            return null;
//        }
//
//        // 实例化布局推荐VO对象
//        LayoutItemInfoVO layoutItemInfoVO = new LayoutItemInfoVO();
//        BeanUtils.copyProperties(layoutItem, layoutItemInfoVO);
//
//        // 推荐图片处理
//        if (StringUtils.isNotEmpty(layoutItemInfoVO.getImage())) {
//            layoutItemInfoVO.setImage(CommonUtils.getFileURL(layoutItemInfoVO.getImage()));
//        }
//        // 业务类型
//        if (layoutItem.getType().equals(1)) {
//            // 文章
//            Article article = articleService.getInfo(layoutItemInfoVO.getTypeId());
//            if (StringUtils.isNotNull(article)) {
//                // 业务标题
//                layoutItemInfoVO.setTypeText(article.getTitle());
//                // 业务图片
//                if (StringUtils.isEmpty(layoutItemInfoVO.getImage())) {
//                    layoutItemInfoVO.setImage(article.getCover());
//                }
//            }
//        }
//
//        // 返回结果
//        return layoutItemInfoVO;
//    }
}
