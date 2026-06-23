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

package com.xiaomayi.web.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xiaomayi.web.dto.article.ArticlePageDTO;
import com.xiaomayi.web.entity.Article;
import com.xiaomayi.web.entity.Category;
import com.xiaomayi.web.enums.ModelViewEnum;
import com.xiaomayi.web.service.*;
import com.xiaomayi.web.vo.HomeArticleVO;
import com.xiaomayi.web.vo.ad.AdListVO;
import com.xiaomayi.web.vo.article.ArticleInfoVO;
import com.xiaomayi.web.vo.layoutitem.LayoutItemListVO;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 网站首页 前端控制器
 * </p>
 *
 * @author 小蚂蚁云团队
 * @since 2024-06-30
 */
@Controller
@Tag(name = "网站首页", description = "网站首页")
@AllArgsConstructor
public class IndexController extends BaseController {

    private final IndexService indexService;
    private final AdService adService;
    private final LayoutItemService layoutItemService;
    private final ArticleService articleService;
    private final CategoryService categoryService;
    private final TagService tagService;

    /**
     * 网站首页
     *
     * @param model 视图模型
     * @return 返回结果
     */
    @GetMapping({"/", "/index.html"})
    public String index(Model model) {
        model.addAttribute("index", "index");

        // 第一块：轮播广告
        List<AdListVO> adList = adService.getAdList(tenantId, 1, 1, 4);
        model.addAttribute("adList", adList);

        // 第二块：头条推荐
        List<LayoutItemListVO> layoutItemList = layoutItemService.getLayoutItemList(tenantId, 1, 1, 1, 7);
        model.addAttribute("layoutItemList", layoutItemList);

        // 第三块：快讯
        HomeArticleVO homeArticleVO3 = indexService.getArticleList(tenantId, 1, 7);
        model.addAttribute("homeArticleVO3", homeArticleVO3);

        // 第四块：产业新闻
        HomeArticleVO homeArticleVO4 = indexService.getArticleList(tenantId, 1, 8);
        model.addAttribute("homeArticleVO4", homeArticleVO4);

        // 第五块：知趣科技
        HomeArticleVO homeArticleVO5 = indexService.getArticleList(tenantId, 1, 7);
        model.addAttribute("homeArticleVO5", homeArticleVO5);

        // 第六块：娱乐生活
        HomeArticleVO homeArticleVO6 = indexService.getArticleList(tenantId, 1, 8);
        model.addAttribute("homeArticleVO6", homeArticleVO6);

        // 第七快：自媒体、创业
        List<HomeArticleVO> articleVOList = new ArrayList<>();
        // 设置需要获取的分类ID集合
        Integer[] idList = new Integer[]{1, 1};
        for (Integer id : idList) {
            HomeArticleVO homeArticleVO7 = indexService.getArticleList(tenantId, id, 16);
            articleVOList.add(homeArticleVO7);
        }
        model.addAttribute("articleVOList", articleVOList);

        // 获取标签列表
        List<com.xiaomayi.web.entity.Tag> tagList = tagService.getTagList(tenantId);
        model.addAttribute("tagList", tagList);
        return ModelViewEnum.ARTICLE_INDEX_HTML.getValue();
    }

    /**
     * 新闻列表
     *
     * @param model 视图模型
     * @return 返回结果
     */
    @GetMapping("/list/{categoryId}/{pageNo}.html")
    public String list(ArticlePageDTO articlePageDTO, Model model) {
        // 获取分类信息
        Category categoryInfo = categoryService.getInfo(articlePageDTO.getCategoryId());
        model.addAttribute("categoryInfo", categoryInfo);
        // 分类ID
        model.addAttribute("categoryId", categoryInfo.getId());
        // 获取文章列表
        Page<Article> pageData = articleService.getArticleList(articlePageDTO);
        model.addAttribute("articleList", pageData.getRecords());
        // 总页数
        model.addAttribute("pages", 20);
        // 当前页码
        model.addAttribute("current", articlePageDTO.getPageNo());

        // 获取本类推荐
        List<LayoutItemListVO> layoutItemList = layoutItemService.getLayoutItemList(tenantId, 2, categoryInfo.getId(), 1, 2);
        model.addAttribute("layoutItemList", layoutItemList);

        // 获取点击率排行
        List<Article> clickList = articleService.getClickList(0, categoryInfo.getId());
        model.addAttribute("clickList", clickList);

        // 获取标签列表
        List<com.xiaomayi.web.entity.Tag> tagList = tagService.getTagList(tenantId);
        model.addAttribute("tagList", tagList);

        // 获取猜猜你喜欢文章列表
        List<Article> likeList = articleService.getLikeList(tenantId, categoryInfo.getId());
        model.addAttribute("likeList", likeList);

        // 渲染模板
        return ModelViewEnum.ARTICLE_LIST_HTML.getValue();
    }

    /**
     * 新闻详情
     *
     * @param model 视图模型
     * @return 返回结果
     */
    @GetMapping("/detail/{articleId}.html")
    public String detail(@PathVariable Integer articleId, Model model) {
        // 获取文章详情
        ArticleInfoVO articleInfo = articleService.getArticleInfo(articleId);
        model.addAttribute("articleInfo", articleInfo);
        // 分类ID
        model.addAttribute("categoryId", articleInfo.getCategoryId());

        // 获取上一篇文章
        ArticleInfoVO preInfo = articleService.getPreArticleInfo(articleId);
        model.addAttribute("preInfo", preInfo);

        // 获取下一篇文章
        ArticleInfoVO nextInfo = articleService.getNextArticleInfo(articleId);
        model.addAttribute("nextInfo", nextInfo);

        // 获取相关文章
        List<Article> articleList = articleService.getSampleList(0, articleId);
        model.addAttribute("articleList", articleList);

        // 获取本类推荐
        List<LayoutItemListVO> layoutItemList = layoutItemService.getLayoutItemList(tenantId, 2, articleInfo.getCategoryId(), 1, 2);
        model.addAttribute("layoutItemList", layoutItemList);

        // 获取点击率排行
        List<Article> clickList = articleService.getClickList(0, articleInfo.getCategoryId());
        model.addAttribute("clickList", clickList);

        // 获取标签列表
        List<com.xiaomayi.web.entity.Tag> tagList = tagService.getTagList(tenantId);
        model.addAttribute("tagList", tagList);

        // 获取猜猜你喜欢文章列表
        List<Article> likeList = articleService.getLikeList(tenantId, articleInfo.getCategoryId());
        model.addAttribute("likeList", likeList);

        // 渲染模板
        return ModelViewEnum.ARTICLE_DETAIL_HTML.getValue();
    }

}
