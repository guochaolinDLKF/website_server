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
import com.xiaomayi.system.dto.filetemplate.FileTemplateAddDTO;
import com.xiaomayi.system.dto.filetemplate.FileTemplatePageDTO;
import com.xiaomayi.system.dto.filetemplate.FileTemplateUpdateDTO;
import com.xiaomayi.system.entity.FileTemplate;
import com.xiaomayi.system.mapper.FileTemplateMapper;
import com.xiaomayi.system.service.FileTemplateService;
import com.xiaomayi.system.vo.filetemplate.FileTemplateInfoVO;
import com.xiaomayi.system.vo.filetemplate.FileTemplateListVO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 文件模板 服务实现类
 * </p>
 *
 * @author 小蚂蚁云团队
 * @since 2024-06-18
 */
@Service
public class FileTemplateServiceImpl extends ServiceImpl<FileTemplateMapper, FileTemplate> implements FileTemplateService {

    /**
     * 查询分页列表
     *
     * @param fileTemplatePageDTO 查询条件
     * @return 返回结果
     */
    @Override
    public Page<FileTemplate> page(FileTemplatePageDTO fileTemplatePageDTO) {
        // 分页设置
        Page<FileTemplate> page = new Page<>(fileTemplatePageDTO.getPageNo(), fileTemplatePageDTO.getPageSize());
        // 查询条件
        LambdaQueryWrapper<FileTemplate> wrapper = new LambdaQueryWrapper<FileTemplate>()
                // 模板名称
                .like(StringUtils.isNotEmpty(fileTemplatePageDTO.getName()), FileTemplate::getName, fileTemplatePageDTO.getName())
                .eq(FileTemplate::getDelFlag, 0)
                .orderByAsc(FileTemplate::getId);
        // 查询分页数据
        Page<FileTemplate> pageData = page(page, wrapper);
        // 遍历数据源
        pageData.convert(item -> {
            // 实例化VO对象
            FileTemplateListVO fileTemplateListVO = new FileTemplateListVO();
            BeanUtils.copyProperties(item, fileTemplateListVO);
            // 文件模板处理
            String filePath = fileTemplateListVO.getFilePath();
            if (!StringUtils.isEmpty(filePath)) {
                fileTemplateListVO.setFilePath(CommonUtils.getFileURL(filePath));
            }
            return fileTemplateListVO;
        });
        // 返回结果
        return pageData;
    }

    /**
     * 根据ID查询信息
     *
     * @param id 文件模板ID
     * @return 返回结果
     */
    @Override
    public FileTemplate getInfo(Integer id) {
        FileTemplate fileTemplate = getById(id);
        if (StringUtils.isNull(fileTemplate) || !fileTemplate.getDelFlag().equals(0)) {
            return null;
        }
        return fileTemplate;
    }

    /**
     * 根据ID查询详情
     *
     * @param id 文件模板ID
     * @return 返回结果
     */
    @Override
    public FileTemplateInfoVO getDetail(Integer id) {
        FileTemplate fileTemplate = getInfo(id);
        if (StringUtils.isNull(fileTemplate)) {
            return null;
        }
        // 实例化VO
        FileTemplateInfoVO fileTemplateInfoVO = new FileTemplateInfoVO();
        BeanUtils.copyProperties(fileTemplate, fileTemplateInfoVO);

        // 文件模板处理
        String filePath = fileTemplate.getFilePath();
        if (!StringUtils.isEmpty(filePath)) {
            fileTemplate.setFilePath(CommonUtils.getFileURL(filePath));
        }

        // 返回结果
        return fileTemplateInfoVO;
    }

    /**
     * 添加文件模板
     *
     * @param fileTemplateAddDTO 参数
     * @return 返回结果
     */
    @Override
    public R add(FileTemplateAddDTO fileTemplateAddDTO) {
        // 检查模板编码是否已存在
        if (checkExist(fileTemplateAddDTO.getCode(), 0)) {
            return R.failed("模板编码已存在");
        }
        // 实例化对象
        FileTemplate fileTemplate = new FileTemplate();
        // 属性拷贝
        BeanUtils.copyProperties(fileTemplateAddDTO, fileTemplate);
        // 模板文件处理
        String filePath = fileTemplateAddDTO.getFilePath();
        if (StringUtils.isNotEmpty(filePath) && filePath.contains(AppConfig.getDomain())) {
            fileTemplate.setFilePath(filePath.replaceAll(AppConfig.getDomain(), ""));
        }
        boolean result = save(fileTemplate);
        if (!result) {
            return R.failed();
        }
        return R.ok();
    }

    /**
     * 更新文件模板
     *
     * @param fileTemplateUpdateDTO 参数
     * @return 返回结果
     */
    @Override
    public R update(FileTemplateUpdateDTO fileTemplateUpdateDTO) {
        // 根据ID查询信息
        FileTemplate fileTemplate = getInfo(fileTemplateUpdateDTO.getId());
        if (StringUtils.isNull(fileTemplate)) {
            return R.failed("记录不存在");
        }
        // 检查模板编码是否已存在
        if (checkExist(fileTemplateUpdateDTO.getCode(), fileTemplateUpdateDTO.getId())) {
            return R.failed("模板编码已存在");
        }
        // 属性拷贝
        BeanUtils.copyProperties(fileTemplateUpdateDTO, fileTemplate);
        // 模板文件处理
        String filePath = fileTemplateUpdateDTO.getFilePath();
        if (StringUtils.isNotEmpty(filePath) && filePath.contains(AppConfig.getDomain())) {
            fileTemplate.setFilePath(filePath.replaceAll(AppConfig.getDomain(), ""));
        }
        boolean result = updateById(fileTemplate);
        if (!result) {
            return R.failed();
        }
        return R.ok();
    }

    /**
     * 删除文件模板
     *
     * @param id 文件模板ID
     * @return 返回结果
     */
    @Override
    public R delete(Integer id) {
        // 根据ID查询信息
        FileTemplate fileTemplate = getInfo(id);
        if (StringUtils.isNull(fileTemplate)) {
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
     * 批量删除文件模板
     *
     * @param idList 文件模板ID
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
        FileTemplate fileTemplate = getOne(new LambdaQueryWrapper<FileTemplate>()
                .eq(FileTemplate::getCode, code)
                .ne(id > 0, FileTemplate::getId, id)
                .eq(FileTemplate::getDelFlag, 0), false);
        return StringUtils.isNotNull(fileTemplate);
    }

    /**
     * 根据模板编码获取模板信息
     *
     * @param code 模板编号
     * @return 返回结果
     */
    @Override
    public FileTemplate getTemplateByCode(String code) {
        // 根据编码查询模板
        FileTemplate fileTemplate = getOne(new LambdaQueryWrapper<FileTemplate>()
                .eq(FileTemplate::getCode, code)
                .eq(FileTemplate::getDelFlag, 0), false);

        if (StringUtils.isNotNull(fileTemplate)) {
            // 文件模板处理
            String filePath = fileTemplate.getFilePath();
            if (!StringUtils.isEmpty(filePath)) {
                fileTemplate.setFilePath(CommonUtils.getFileURL(filePath));
            }
        }

        // 返回结果
        return fileTemplate;
    }
}
