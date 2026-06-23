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
import com.xiaomayi.cms.dto.layoutitem.LayoutItemAddDTO;
import com.xiaomayi.cms.dto.layoutitem.LayoutItemPageDTO;
import com.xiaomayi.cms.dto.layoutitem.LayoutItemUpdateDTO;
import com.xiaomayi.cms.entity.Article;
import com.xiaomayi.cms.entity.Layout;
import com.xiaomayi.cms.entity.LayoutItem;
import com.xiaomayi.cms.mapper.LayoutItemMapper;
import com.xiaomayi.cms.service.ArticleService;
import com.xiaomayi.cms.service.LayoutItemService;
import com.xiaomayi.cms.service.LayoutService;
import com.xiaomayi.cms.vo.layoutitem.LayoutItemInfoVO;
import com.xiaomayi.cms.vo.layoutitem.LayoutItemListVO;
import com.xiaomayi.cms.vo.layoutitem.LayoutItemVO;
import com.xiaomayi.core.config.AppConfig;
import com.xiaomayi.core.utils.CommonUtils;
import com.xiaomayi.core.utils.R;
import com.xiaomayi.core.utils.StringUtils;
import com.xiaomayi.system.entity.Notice;
import com.xiaomayi.system.service.NoticeService;
import com.xiaomayi.system.utils.DictResolver;
import lombok.AllArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

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
    private final LayoutService layoutService;
    private final NoticeService noticeService;

    /**
     * 查询分页列表
     *
     * @param layoutItemPageDTO 查询条件
     * @return 返回结果
     */
    @Override
    public Page<LayoutItem> page(LayoutItemPageDTO layoutItemPageDTO) {
        // 分页设置
        Page<LayoutItem> page = new Page<>(layoutItemPageDTO.getPageNo(), layoutItemPageDTO.getPageSize());
        // 查询条件
        LambdaQueryWrapper<LayoutItem> wrapper = new LambdaQueryWrapper<LayoutItem>()
                // 推荐类型：1-CMS文章 2-通知公告
                .eq(StringUtils.isNotNull(layoutItemPageDTO.getType()) && layoutItemPageDTO.getType() > 0, LayoutItem::getType, layoutItemPageDTO.getType())
                // 布局ID
                .eq(StringUtils.isNotNull(layoutItemPageDTO.getLayoutId()) && layoutItemPageDTO.getLayoutId() > 0, LayoutItem::getLayoutId, layoutItemPageDTO.getLayoutId())
                .eq(LayoutItem::getDelFlag, 0)
                .orderByAsc(LayoutItem::getId);
        // 查询分页数据
        Page<LayoutItem> pageData = page(page, wrapper);
        // 遍历数据源
        pageData.convert(item -> {
            // 实例化VO对象
            LayoutItemListVO layoutItemListVO = new LayoutItemListVO();
            BeanUtils.copyProperties(item, layoutItemListVO);

            // 推荐图片处理
            if (StringUtils.isNotEmpty(layoutItemListVO.getImage())) {
                layoutItemListVO.setImage(CommonUtils.getFileURL(layoutItemListVO.getImage()));
            }

            // 推荐类型
            Integer type = layoutItemListVO.getType();
            if (StringUtils.isNotNull(type)) {
                layoutItemListVO.setTypeText(DictResolver.getDictItemName("layout_type", type.toString()));
            }

            // 获取推荐内容信息
            LayoutItemVO layoutItemVO = getLayoutItemInfo(type, layoutItemListVO.getTypeId());
            if (StringUtils.isNotEmpty(layoutItemVO.getTypeTitle())) {
                layoutItemListVO.setTypeTitle(layoutItemVO.getTypeTitle());
            }
            if (StringUtils.isNotEmpty(layoutItemVO.getImage())) {
                layoutItemListVO.setImage(layoutItemVO.getImage());
            }
            if (StringUtils.isNotEmpty(layoutItemVO.getTypeIntro())) {
                layoutItemListVO.setTypeIntro(layoutItemVO.getTypeIntro());
            }

            // 返回结果
            return layoutItemListVO;
        });
        // 返回结果
        return pageData;
    }

    /**
     * 根据ID查询信息
     *
     * @param id 推荐ID
     * @return 返回结果
     */
    @Override
    public LayoutItem getInfo(Integer id) {
        LayoutItem layoutItem = getById(id);
        if (StringUtils.isNull(layoutItem) || !layoutItem.getDelFlag().equals(0)) {
            return null;
        }
        return layoutItem;
    }

    /**
     * 根据ID查询详情
     *
     * @param id 推荐ID
     * @return 返回结果
     */
    @Override
    public LayoutItemInfoVO getDetail(Integer id) {
        LayoutItem layoutItem = getInfo(id);
        if (StringUtils.isNull(layoutItem)) {
            return null;
        }
        // 实例化VO
        LayoutItemInfoVO layoutItemInfoVO = new LayoutItemInfoVO();
        BeanUtils.copyProperties(layoutItem, layoutItemInfoVO);

        // 推荐图片处理
        if (StringUtils.isNotEmpty(layoutItemInfoVO.getImage())) {
            layoutItemInfoVO.setImage(CommonUtils.getFileURL(layoutItemInfoVO.getImage()));
        }

        // 获取推荐内容信息
        LayoutItemVO layoutItemVO = getLayoutItemInfo(layoutItem.getType(), layoutItem.getTypeId());
        if (StringUtils.isNotEmpty(layoutItemVO.getTypeTitle())) {
            layoutItemInfoVO.setTypeText(layoutItemVO.getTypeTitle());
        }
        if (StringUtils.isNotEmpty(layoutItemVO.getImage())) {
            layoutItemInfoVO.setImage(layoutItemVO.getImage());
        }
        if (StringUtils.isNotEmpty(layoutItemVO.getTypeIntro())) {
            layoutItemInfoVO.setTypeIntro(layoutItemVO.getTypeIntro());
        }

        // 返回结果
        return layoutItemInfoVO;
    }

    /**
     * 获取推荐内容信息
     *
     * @param type   推荐类型：1-热点文章 2-通知公告
     * @param typeId 推荐类型ID
     * @return 返回结果
     */
    private LayoutItemVO getLayoutItemInfo(Integer type, Integer typeId) {
        LayoutItemVO layoutItemVO = new LayoutItemVO();
        if (type.equals(1)) {
            // 热门文章
            Article article = articleService.getInfo(typeId);
            if (StringUtils.isNotNull(article)) {
                // 文章标题
                layoutItemVO.setTypeTitle(article.getTitle());
                // 文章导读
                layoutItemVO.setTypeIntro(article.getIntro());
                // 文章封面
                if (StringUtils.isEmpty(article.getCover())) {
                    layoutItemVO.setImage(article.getCover());
                }
            }
        } else if (type.equals(2)) {
            // 通知公告
            Notice notice = noticeService.getInfo(typeId);
            if (StringUtils.isNotNull(notice)) {
                // 通知标题
                layoutItemVO.setTypeTitle(notice.getTitle());
                // 通知封面
                if (StringUtils.isEmpty(notice.getCover())) {
                    layoutItemVO.setImage(notice.getCover());
                }
            }
        }
        // 返回结果
        return layoutItemVO;
    }

    /**
     * 添加页面推荐
     *
     * @param layoutItemAddDTO 参数
     * @return 返回结果
     */
    @Override
    public R add(LayoutItemAddDTO layoutItemAddDTO) {
        // 查询布局
        Layout layout = layoutService.getInfo(layoutItemAddDTO.getLayoutId());
        if (StringUtils.isNull(layout)) {
            return R.failed("布局不存在");
        }
        // 实例化对象
        LayoutItem layoutItem = new LayoutItem();
        // 属性拷贝
        BeanUtils.copyProperties(layoutItemAddDTO, layoutItem);
        layoutItem.setLayoutDescription(layout.getDescription());
        layoutItem.setLayoutLocation(layout.getLocation());
        // 图片处理
        if (StringUtils.isNotEmpty(layoutItem.getImage()) && layoutItem.getImage().contains(AppConfig.getDomain())) {
            layoutItem.setImage(layoutItem.getImage().replaceAll(AppConfig.getDomain(), ""));
        }
        boolean result = save(layoutItem);
        if (!result) {
            return R.failed();
        }
        return R.ok();
    }

    /**
     * 更新页面推荐
     *
     * @param layoutItemUpdateDTO 参数
     * @return 返回结果
     */
    @Override
    public R update(LayoutItemUpdateDTO layoutItemUpdateDTO) {
        // 根据ID查询信息
        LayoutItem layoutItem = getInfo(layoutItemUpdateDTO.getId());
        if (StringUtils.isNull(layoutItem)) {
            return R.failed("记录不存在");
        }
        // 查询布局
        Layout layout = layoutService.getInfo(layoutItemUpdateDTO.getLayoutId());
        if (StringUtils.isNull(layout)) {
            return R.failed("布局不存在");
        }
        // 属性拷贝
        BeanUtils.copyProperties(layoutItemUpdateDTO, layoutItem);
        layoutItem.setLayoutDescription(layout.getDescription());
        layoutItem.setLayoutLocation(layout.getLocation());
        // 图片处理
        if (StringUtils.isNotEmpty(layoutItem.getImage()) && layoutItem.getImage().contains(AppConfig.getDomain())) {
            layoutItem.setImage(layoutItem.getImage().replaceAll(AppConfig.getDomain(), ""));
        }
        boolean result = updateById(layoutItem);
        if (!result) {
            return R.failed();
        }
        return R.ok();
    }

    /**
     * 删除页面推荐
     *
     * @param id 推荐ID
     * @return 返回结果
     */
    @Override
    public R delete(Integer id) {
        // 根据ID查询信息
        LayoutItem layoutItem = getInfo(id);
        if (StringUtils.isNull(layoutItem)) {
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
     * 批量删除页面推荐
     *
     * @param idList 推荐ID
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
