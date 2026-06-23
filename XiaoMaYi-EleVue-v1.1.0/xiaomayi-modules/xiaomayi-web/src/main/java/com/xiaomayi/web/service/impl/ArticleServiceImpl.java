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
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiaomayi.core.utils.CommonUtils;
import com.xiaomayi.core.utils.StringUtils;
import com.xiaomayi.web.dto.article.ArticlePageDTO;
import com.xiaomayi.web.entity.Article;
import com.xiaomayi.web.entity.Category;
import com.xiaomayi.web.mapper.ArticleMapper;
import com.xiaomayi.web.service.ArticleService;
import com.xiaomayi.web.service.CategoryService;
import com.xiaomayi.web.vo.article.ArticleInfoVO;
import com.xiaomayi.web.vo.article.ArticleListVO;
import lombok.AllArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 文章 服务实现类
 * </p>
 *
 * @author 小蚂蚁云团队
 * @since 2024-06-30
 */
@Service
@AllArgsConstructor
public class ArticleServiceImpl extends ServiceImpl<ArticleMapper, Article> implements ArticleService {

    private final CategoryService categoryService;

    /**
     * 根据分类ID获取文章列表
     *
     * @param articlePageDTO 查询条件
     * @return 返回结果
     */
    @Override
    public Page<Article> getArticleList(ArticlePageDTO articlePageDTO) {
        // 租户ID
        Integer tenantId = articlePageDTO.getTenantId();
        // 分类ID
        Integer categoryId = articlePageDTO.getCategoryId();

        // 分页查询条件
        LambdaQueryWrapper<Article> wrapper = new LambdaQueryWrapper<>();
        // 租户ID
        wrapper.eq(StringUtils.isNotNull(tenantId) && tenantId >= 0, Article::getTenantId, tenantId);
        // 分类ID
        wrapper.eq(StringUtils.isNotNull(categoryId) && categoryId > 0, Article::getCategoryId, categoryId);
        // 有效标识
        wrapper.eq(Article::getDelFlag, 0);
        // 排序
        wrapper.orderByAsc(Article::getSort);

        // 设置分页
        Page<Article> page = new Page<>(articlePageDTO.getPageNo(), articlePageDTO.getPageSize());
        // 分页查询
        Page<Article> pageData = page(page, wrapper);
        // 数据转换
        pageData.convert(item -> {
            // 实例化VO
            ArticleListVO articleListVO = new ArticleListVO();
            BeanUtils.copyProperties(item, articleListVO);

            // 文章封面
            if (StringUtils.isNotEmpty(articleListVO.getCover())) {
                articleListVO.setCover(CommonUtils.getFileURL(articleListVO.getCover()));
            }

            // 返回对象
            return articleListVO;
        });

        // 返回结果
        return pageData;
    }

    /**
     * 获取文章列表
     *
     * @param tenantId   租户ID
     * @param categoryId 分类ID
     * @param limit      数量
     * @return 返回结果
     */
    @Override
    public List<Article> getArticleList(Integer tenantId, Integer categoryId, Integer limit) {
        List<Article> articleList = list(new LambdaQueryWrapper<Article>()
                .eq(Article::getTenantId, tenantId)
                .eq(Article::getCategoryId, categoryId)
                .orderByDesc(Article::getId)
                .last("limit " + limit));

        // 数据源判空
        if (StringUtils.isNotEmpty(articleList)) {
            // 历数据源
            for (Article article : articleList) {
                // 文章封面
                if (StringUtils.isNotEmpty(article.getCover())) {
                    article.setCover(CommonUtils.getFileURL(article.getCover()));
                }
            }
        }
        return articleList;
    }

    /**
     * 根据文章ID获取信息
     *
     * @param articleId 文章ID
     * @return 返回结果
     */
    @Override
    public Article getInfo(Integer articleId) {
        // 查询文章信息
        Article article = getById(articleId);
        if (StringUtils.isNull(article) || !article.getDelFlag().equals(0)) {
            return null;
        }
        // 文章封面
        if (StringUtils.isNotEmpty(article.getCover())) {
            article.setCover(CommonUtils.getFileURL(article.getCover()));
        }
        return article;
    }

    /**
     * 根据文章ID获取详情
     *
     * @param articleId 文章ID
     * @return 返回结果
     */
    @Override
    public ArticleInfoVO getArticleInfo(Integer articleId) {
        // 获取文章信息
        Article article = getInfo(articleId);
        if (StringUtils.isNull(article)) {
            return null;
        }

        // 实例化文章VO对象
        ArticleInfoVO articleInfoVO = new ArticleInfoVO();
        BeanUtils.copyProperties(article, articleInfoVO);

        // 富文本处理
        if (StringUtils.isNotEmpty(articleInfoVO.getContent())) {
            articleInfoVO.setContent(CommonUtils.getContent(articleInfoVO.getContent()));
        }

        // 获取分类信息
        Category category = categoryService.getInfo(article.getCategoryId());
        if (StringUtils.isNotNull(category)) {
            articleInfoVO.setCategoryName(category.getName());
        }

        // 返回结果
        return articleInfoVO;
    }

    /**
     * 获取上一篇文章
     *
     * @param articleId 文章ID
     * @return 返回结果
     */
    @Override
    public ArticleInfoVO getPreArticleInfo(Integer articleId) {
        // 获取上一篇文章
        Article article = getOne(new LambdaQueryWrapper<Article>()
                .lt(Article::getId, articleId)
                .eq(Article::getDelFlag, 0), false);
        if (StringUtils.isNull(article)) {
            return null;
        }

        // 查询上一篇文章
        Article articleInfo = getInfo(article.getId());
        if (StringUtils.isNull(articleInfo)) {
            return null;
        }
        // 实例化VO对象
        ArticleInfoVO articleInfoVO = new ArticleInfoVO();
        BeanUtils.copyProperties(articleInfo, articleInfoVO);

        // 返回结果
        return articleInfoVO;
    }

    /**
     * 获取下一篇文章
     *
     * @param articleId 文章ID
     * @return 返回结果
     */
    @Override
    public ArticleInfoVO getNextArticleInfo(Integer articleId) {
        // 获取上一篇文章
        Article article = getOne(new LambdaQueryWrapper<Article>()
                .gt(Article::getId, articleId)
                .eq(Article::getDelFlag, 0), false);
        if (StringUtils.isNull(article)) {
            return null;
        }

        // 查询上一篇文章
        Article articleInfo = getInfo(article.getId());
        if (StringUtils.isNull(articleInfo)) {
            return null;
        }
        // 实例化VO对象
        ArticleInfoVO articleInfoVO = new ArticleInfoVO();
        BeanUtils.copyProperties(articleInfo, articleInfoVO);

        // 返回结果
        return articleInfoVO;
    }

    /**
     * 获取相关文章
     *
     * @param tenantId  租户ID
     * @param articleId 文章ID
     * @return 返回结果
     */
    @Override
    public List<Article> getSampleList(Integer tenantId, Integer articleId) {
        // 根据文章ID查询信息
        Article article = getInfo(articleId);
        if (StringUtils.isNull(article)) {
            return null;
        }
        // 获取相关文章列表
        List<Article> articleList = list(new LambdaQueryWrapper<Article>()
                // 租户ID
                .eq(Article::getTenantId, tenantId)
                // 文章ID
                .eq(Article::getCategoryId, article.getCategoryId())
                // 相似文章排除当前文章
                .ne(Article::getId, articleId)
                // 根据时间倒序排列
                .orderByDesc(Article::getCreateTime)
                // 取前10条数据
                .last("limit 10"));
        // 返回结果
        return articleList;
    }

    /**
     * 获取某个分类的点击排行文章列表
     *
     * @param tenantId   租户ID
     * @param categoryId 分类ID
     * @return 返回结果
     */
    @Override
    public List<Article> getClickList(Integer tenantId, Integer categoryId) {
        // 获取本分类点击率排行
        List<Article> articleList = list(new LambdaQueryWrapper<Article>()
                // 租户ID
                .eq(Article::getTenantId, tenantId)
                // 文章ID
                .eq(Article::getCategoryId, categoryId)
                // 点击率降序排列
                .orderByDesc(Article::getClick)
                // 取前10条数据
                .last("limit 9"));
        // 查询结果判空
        if (StringUtils.isNotEmpty(articleList)) {
            // 遍历数据源
            articleList.forEach(item -> {
                item.setCover(CommonUtils.getFileURL(item.getCover()));
            });
        }
        // 返回结果
        return articleList;
    }

    /**
     * 获取猜猜你喜欢文章列表
     *
     * @param tenantId   租户ID
     * @param categoryId 分类ID
     * @return 返回结果
     */
    @Override
    public List<Article> getLikeList(Integer tenantId, Integer categoryId) {
        // 从本分类中获取文章列表
        List<Article> articleList = list(new LambdaQueryWrapper<Article>()
                // 租户ID
                .eq(Article::getTenantId, tenantId)
                // 文章ID
                .eq(Article::getCategoryId, categoryId)
                // 点击率降序排列
                .orderByDesc(Article::getCreateTime)
                // 取前10条数据
                .last("limit 6"));
        // 返回结果
        return articleList;
    }
}
