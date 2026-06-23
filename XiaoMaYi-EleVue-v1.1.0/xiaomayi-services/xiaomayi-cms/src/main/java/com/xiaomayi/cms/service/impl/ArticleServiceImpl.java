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
import com.xiaomayi.cms.dto.article.ArticleAddDTO;
import com.xiaomayi.cms.dto.article.ArticlePageDTO;
import com.xiaomayi.cms.dto.article.ArticleUpdateDTO;
import com.xiaomayi.cms.entity.Article;
import com.xiaomayi.cms.entity.Category;
import com.xiaomayi.cms.mapper.ArticleMapper;
import com.xiaomayi.cms.service.ArticleService;
import com.xiaomayi.cms.service.CategoryService;
import com.xiaomayi.cms.vo.article.ArticleInfoVO;
import com.xiaomayi.cms.vo.article.ArticleListVO;
import com.xiaomayi.core.config.AppConfig;
import com.xiaomayi.core.utils.CommonUtils;
import com.xiaomayi.core.utils.R;
import com.xiaomayi.core.utils.StringUtils;
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
 * @since 2024-06-24
 */
@Service
@AllArgsConstructor
public class ArticleServiceImpl extends ServiceImpl<ArticleMapper, Article> implements ArticleService {

    private final CategoryService categoryService;

    /**
     * 查询分页列表
     *
     * @param articlePageDTO 查询条件
     * @return 返回结果
     */
    @Override
    public Page<Article> page(ArticlePageDTO articlePageDTO) {
        // 分页设置
        Page<Article> page = new Page<>(articlePageDTO.getPageNo(), articlePageDTO.getPageSize());
        // 查询条件
        LambdaQueryWrapper<Article> wrapper = new LambdaQueryWrapper<Article>()
                // 文章标题
                .like(StringUtils.isNotEmpty(articlePageDTO.getTitle()), Article::getTitle, articlePageDTO.getTitle())
                // 文章状态：0-正常 1-下架
                .eq(StringUtils.isNotNull(articlePageDTO.getStatus()) && articlePageDTO.getStatus() > 0, Article::getStatus, articlePageDTO.getStatus())
                .eq(Article::getDelFlag, 0)
                .orderByDesc(Article::getId);
        // 查询分页数据
        Page<Article> pageData = page(page, wrapper);
        // 遍历数据源
        pageData.convert(item -> {
            // 实例化VO对象
            ArticleListVO articleListVO = new ArticleListVO();
            BeanUtils.copyProperties(item, articleListVO);
            // 分类ID
            Integer categoryId = articleListVO.getCategoryId();
            if (StringUtils.isNotNull(categoryId) && categoryId > 0) {
                // 查询分类信息
                Category category = categoryService.getInfo(categoryId);
                if (StringUtils.isNotNull(category)) {
                    articleListVO.setCategoryName(category.getName());
                }
            }
            // 文章封面
            if (StringUtils.isNotEmpty(articleListVO.getCover())) {
                articleListVO.setCover(CommonUtils.getFileURL(articleListVO.getCover()));
            }
            // 返回结果
            return articleListVO;
        });
        // 返回结果
        return pageData;
    }

    /**
     * 根据ID查询信息
     *
     * @param id 文章ID
     * @return 返回结果
     */
    @Override
    public Article getInfo(Integer id) {
        Article article = getById(id);
        if (StringUtils.isNull(article) || !article.getDelFlag().equals(0)) {
            return null;
        }
        // 文章图片
        if (StringUtils.isNotEmpty(article.getCover())) {
            article.setCover(CommonUtils.getFileURL(article.getCover()));
        }
        return article;
    }

    /**
     * 根据ID查询详情
     *
     * @param id 文章ID
     * @return 返回结果
     */
    @Override
    public ArticleInfoVO getDetail(Integer id) {
        Article article = getInfo(id);
        if (StringUtils.isNull(article)) {
            return null;
        }
        // 实例化VO
        ArticleInfoVO articleInfoVO = new ArticleInfoVO();
        BeanUtils.copyProperties(article, articleInfoVO);

        // 图集处理
        String images = articleInfoVO.getImages();
        if (StringUtils.isNotEmpty(images)) {
            List<String> imagesList = CommonUtils.getFilesList(images);
            articleInfoVO.setImagesList(imagesList);
        }

        // 富文本处理
        if (StringUtils.isNotEmpty(articleInfoVO.getContent())) {
            articleInfoVO.setContent(CommonUtils.getContent(articleInfoVO.getContent()));
        }

        // 返回结果
        return articleInfoVO;
    }

    /**
     * 添加文章
     *
     * @param articleAddDTO 参数
     * @return 返回结果
     */
    @Override
    public R add(ArticleAddDTO articleAddDTO) {
        // 实例化对象
        Article article = new Article();
        // 属性拷贝
        BeanUtils.copyProperties(articleAddDTO, article);
        // 封面处理
        String cover = articleAddDTO.getCover();
        if (StringUtils.isNotEmpty(cover) && cover.contains(AppConfig.getDomain())) {
            article.setCover(cover.replaceAll(AppConfig.getDomain(), ""));
        }
        // 文章图集处理
        String images = articleAddDTO.getImages();
        if (StringUtils.isNotEmpty(images) && images.contains(AppConfig.getDomain())) {
            article.setImages(images.replaceAll(AppConfig.getDomain(), ""));
        }
        // 富文本处理
        String content = articleAddDTO.getContent();
        if (StringUtils.isNotEmpty(content)) {
            article.setContent(content.replaceAll(AppConfig.getDomain(), ""));
        }
        boolean result = save(article);
        if (!result) {
            return R.failed();
        }
        return R.ok();
    }

    /**
     * 更新文章
     *
     * @param articleUpdateDTO 参数
     * @return 返回结果
     */
    @Override
    public R update(ArticleUpdateDTO articleUpdateDTO) {
        // 根据ID查询信息
        Article article = getInfo(articleUpdateDTO.getId());
        if (StringUtils.isNull(article)) {
            return R.failed("记录不存在");
        }
        // 属性拷贝
        BeanUtils.copyProperties(articleUpdateDTO, article);
        // 封面处理
        String cover = articleUpdateDTO.getCover();
        if (StringUtils.isNotEmpty(cover) && cover.contains(AppConfig.getDomain())) {
            article.setCover(cover.replaceAll(AppConfig.getDomain(), ""));
        }
        // 文章图集处理
        String images = articleUpdateDTO.getImages();
        if (StringUtils.isNotEmpty(images) && images.contains(AppConfig.getDomain())) {
            article.setImages(images.replaceAll(AppConfig.getDomain(), ""));
        }
        // 富文本处理
        String content = articleUpdateDTO.getContent();
        if (StringUtils.isNotEmpty(content)) {
            article.setContent(content.replaceAll(AppConfig.getDomain(), ""));
        }
        boolean result = updateById(article);
        if (!result) {
            return R.failed();
        }
        return R.ok();
    }

    /**
     * 删除文章
     *
     * @param id 文章ID
     * @return 返回结果
     */
    @Override
    public R delete(Integer id) {
        // 根据ID查询信息
        Article article = getInfo(id);
        if (StringUtils.isNull(article)) {
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
     * 批量删除文章
     *
     * @param idList 文章ID
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
