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

package com.xiaomayi.generator.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiaomayi.core.utils.R;
import com.xiaomayi.core.utils.StringUtils;
import com.xiaomayi.generator.dto.template.TemplateAddDTO;
import com.xiaomayi.generator.dto.template.TemplatePageDTO;
import com.xiaomayi.generator.dto.template.TemplateUpdateDTO;
import com.xiaomayi.generator.entity.Template;
import com.xiaomayi.generator.mapper.TemplateMapper;
import com.xiaomayi.generator.service.TemplateService;
import com.xiaomayi.generator.utils.FileUtils;
import com.xiaomayi.generator.vo.template.TemplateInfoVO;
import com.xiaomayi.generator.vo.template.TemplateListVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 模板 服务实现类
 * </p>
 *
 * @author 小蚂蚁云团队
 * @since 2024-06-18
 */
@Slf4j
@Service
public class TemplateServiceImpl extends ServiceImpl<TemplateMapper, Template> implements TemplateService {

    /**
     * 查询分页列表
     *
     * @param templatePageDTO 查询条件
     * @return 返回结果
     */
    @Override
    public Page<Template> page(TemplatePageDTO templatePageDTO) {
        // 分页设置
        Page<Template> page = new Page<>(templatePageDTO.getPageNo(), templatePageDTO.getPageSize());
        // 查询条件
        LambdaQueryWrapper<Template> wrapper = new LambdaQueryWrapper<Template>()
                // 模板名称
                .like(StringUtils.isNotEmpty(templatePageDTO.getName()), Template::getName, templatePageDTO.getName())
                .eq(Template::getDelFlag, 0)
                .orderByAsc(Template::getId);
        // 查询分页数据
        Page<Template> pageData = page(page, wrapper);
        // 遍历数据源
        pageData.convert(item -> {
            // 实例化VO对象
            TemplateListVO templateListVO = new TemplateListVO();
            BeanUtils.copyProperties(item, templateListVO);
            return templateListVO;
        });
        // 返回结果
        return pageData;
    }

    /**
     * 根据ID查询信息
     *
     * @param id 模板ID
     * @return 返回结果
     */
    @Override
    public Template getInfo(Integer id) {
        Template template = getById(id);
        if (StringUtils.isNull(template) || !template.getDelFlag().equals(0)) {
            return null;
        }
        return template;
    }

    /**
     * 根据ID查询详情
     *
     * @param id 模板ID
     * @return 返回结果
     */
    @Override
    public TemplateInfoVO getDetail(Integer id) {
        Template template = getInfo(id);
        if (StringUtils.isNull(template)) {
            return null;
        }
        // 实例化VO
        TemplateInfoVO templateInfoVO = new TemplateInfoVO();
        BeanUtils.copyProperties(template, templateInfoVO);
        return templateInfoVO;
    }

    /**
     * 添加模板
     *
     * @param templateAddDTO 参数
     * @return 返回结果
     */
    @Override
    public R add(TemplateAddDTO templateAddDTO) {
        // 检查模板名称是否已存在
        if (checkExist(templateAddDTO.getName(), 0)) {
            return R.failed("模板名称已存在");
        }
        // 实例化对象
        Template template = new Template();
        // 属性拷贝
        BeanUtils.copyProperties(templateAddDTO, template);
        boolean result = save(template);
        if (!result) {
            return R.failed();
        }
        return R.ok();
    }

    /**
     * 更新模板
     *
     * @param templateUpdateDTO 参数
     * @return 返回结果
     */
    @Override
    public R update(TemplateUpdateDTO templateUpdateDTO) {
        // 根据ID查询信息
        Template template = getInfo(templateUpdateDTO.getId());
        if (StringUtils.isNull(template)) {
            return R.failed("记录不存在");
        }
        // 检查模板名称是否已存在
        if (checkExist(templateUpdateDTO.getName(), templateUpdateDTO.getId())) {
            return R.failed("模板名称已存在");
        }
        // 属性拷贝
        BeanUtils.copyProperties(templateUpdateDTO, template);
        boolean result = updateById(template);
        if (!result) {
            return R.failed();
        }

        // 获取文件模块绝对路径
        String filePath = this.getClass().getProtectionDomain().getCodeSource().getLocation().getPath();
        FileUtils.writeFile(filePath, template.getCode(), template.getContent());
        // 返回结果
        return R.ok();
    }

    /**
     * 删除模板
     *
     * @param id 模板ID
     * @return 返回结果
     */
    @Override
    public R delete(Integer id) {
        // 根据ID查询信息
        Template template = getInfo(id);
        if (StringUtils.isNull(template)) {
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
     * 批量删除模板
     *
     * @param idList 模板ID
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

    /**
     * 检查模板编码是否已存在
     *
     * @param code 模板编码
     * @param id   模板ID
     * @return 返回结果
     */
    @Override
    public boolean checkExist(String code, Integer id) {
        Template fileTemplate = getOne(new LambdaQueryWrapper<Template>()
                .eq(Template::getCode, code)
                .ne(id > 0, Template::getId, id)
                .eq(Template::getDelFlag, 0), false);
        return StringUtils.isNotNull(fileTemplate);
    }
}
