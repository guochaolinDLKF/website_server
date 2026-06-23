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
import com.xiaomayi.core.utils.StringUtils;
import com.xiaomayi.web.entity.Category;
import com.xiaomayi.web.mapper.CategoryMapper;
import com.xiaomayi.web.service.CategoryService;
import com.xiaomayi.web.vo.category.CategoryListVO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 文章分类 服务实现类
 * </p>
 *
 * @author 小蚂蚁云团队
 * @since 2024-06-30
 */
@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    /**
     * 获取分类列表
     *
     * @param tenantId 租户ID
     * @param parentId 上级ID
     * @return 返回结果
     */
    @Override
    public List<CategoryListVO> getCategoryList(Integer tenantId, Integer parentId) {
        // 获取分类列表
        List<Category> categoryList = list(new LambdaQueryWrapper<Category>()
                // 租户ID
                .eq(Category::getTenantId, tenantId)
                // 上级ID
                .eq(Category::getParentId, parentId)
                .eq(Category::getDelFlag, 0)
                .orderByAsc(Category::getSort));
        // 实例化分类VO列表
        List<CategoryListVO> categoryListVOS = new ArrayList<>();
        // 查询结果判空
        if (!categoryList.isEmpty()) {
            // 遍历分类数据
            for (Category category : categoryList) {
                CategoryListVO categoryListVO = new CategoryListVO();
                BeanUtils.copyProperties(category, categoryListVO);
                // 获取子级分类列表
                List<CategoryListVO> childrenList = getCategoryList(tenantId, category.getId());
                // 设置子级分类
                categoryListVO.setChildren(childrenList);
                // 加入列表
                categoryListVOS.add(categoryListVO);
            }
        }
        // 返回结果
        return categoryListVOS;
    }

    /**
     * 根据分类ID查询信息
     *
     * @param categoryId 分类ID
     * @return 返回结果
     */
    @Override
    public Category getInfo(Integer categoryId) {
        Category category = getById(categoryId);
        if (StringUtils.isNull(category) || !category.getDelFlag().equals(0)) {
            return null;
        }
        return category;
    }
}