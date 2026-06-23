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

package ${package.ServiceImpl};

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xiaomayi.core.config.AppConfig;
import com.xiaomayi.core.utils.CommonUtils;
import com.xiaomayi.core.utils.R;
import com.xiaomayi.core.utils.StringUtils;
import ${packageName}.dto.${entity?lower_case}.${table.entityName}AddDTO;
import ${packageName}.dto.${entity?lower_case}.${table.entityName}ListDTO;
import ${packageName}.dto.${entity?lower_case}.${table.entityName}PageDTO;
import ${packageName}.dto.${entity?lower_case}.${table.entityName}UpdateDTO;
import ${package.Entity}.${entity};
import ${package.Mapper}.${table.mapperName};
import ${package.Service}.${table.serviceName};
import ${superServiceImplClassPackage};
import ${packageName}.vo.${entity?lower_case}.${table.entityName}InfoVO;
import ${packageName}.vo.${entity?lower_case}.${table.entityName}ListVO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * ${table.comment!} 服务实现类
 * </p>
 *
 * @author ${author}
 * @since ${date}
 */
@Service
<#if kotlin>
open class ${table.serviceImplName} : ${superServiceImplClass}<${table.mapperName}, ${entity}>(), ${table.serviceName} {

}
<#else>
public class ${table.serviceImplName} extends ${superServiceImplClass}<${table.mapperName}, ${entity}> implements ${table.serviceName} {

    /**
     * 查询分页列表
     *
     * @param ${table.entityName?uncap_first}PageDTO 查询条件
     * @return 返回结果
     */
    @Override
    public Page<${table.entityName}> page(${table.entityName}PageDTO ${table.entityName?uncap_first}PageDTO) {
        // 分页设置
        Page<${table.entityName}> page = new Page<>(${table.entityName?uncap_first}PageDTO.getPageNo(), ${table.entityName?uncap_first}PageDTO.getPageSize());
        // 查询条件
        LambdaQueryWrapper<${table.entityName}> wrapper = new LambdaQueryWrapper<${table.entityName}>()
			<#------------  BEGIN 字段循环遍历  ---------->
			<#list table.fields as field>
				<#if field.comment?contains('名称') || field.comment?contains('标题') || field.comment?contains('编码') || field.comment?contains('状态') || field.comment?contains('类型')>
				<#if field.comment!?length gt 0>
				// ${field.comment}
				</#if>
				<#if field.columnType == "INTEGER">
				.eq(StringUtils.isNotNull(${table.entityName?uncap_first}PageDTO.get${field.propertyName?cap_first}()) && ${table.entityName?uncap_first}PageDTO.get${field.propertyName?cap_first}() > 0, ${table.entityName}::get${field.propertyName?cap_first}, ${table.entityName?uncap_first}PageDTO.get${field.propertyName?cap_first}())
				<#elseif field.columnType == "STRING">
				.like(StringUtils.isNotEmpty(${table.entityName?uncap_first}PageDTO.get${field.propertyName?cap_first}()), ${table.entityName}::get${field.propertyName?cap_first}, ${table.entityName?uncap_first}PageDTO.get${field.propertyName?cap_first}())
				</#if>
				</#if>
			</#list>
			<#------------  END 字段循环遍历  ---------->
				.eq(${table.entityName}::getDelFlag, 0)
                .orderByAsc(${table.entityName}::getId);
        // 查询分页数据
        Page<${table.entityName}> pageData = page(page, wrapper);
        pageData.convert(item -> {
            // 实例化VO对象
            ${table.entityName}ListVO ${table.entityName?uncap_first}ListVO = new ${table.entityName}ListVO();
            BeanUtils.copyProperties(item, ${table.entityName?uncap_first}ListVO);
        <#------------  BEGIN 字段循环遍历  ---------->
        <#list table.fields as field>
            <#if field.propertyName == 'cover' || field.propertyName == 'logo' || field.propertyName == 'image'>
            // ${field.comment}
            String ${field.propertyName} = ${table.entityName?uncap_first}ListVO.get${field.propertyName?cap_first}();
            if (StringUtils.isNotEmpty(${field.propertyName})) {
                ${table.entityName?uncap_first}ListVO.set${field.propertyName?cap_first}(CommonUtils.getFileURL(${field.propertyName}));
            }
            </#if>
        </#list>
        <#------------  END 字段循环遍历  ---------->
            return ${table.entityName?uncap_first}ListVO;
        });
        // 返回结果
        return pageData;
    }

	/**
     * 查询数据列表
     *
     * @param ${table.entityName?uncap_first}ListDTO 查询条件
     * @return 返回结果
     */
    @Override
    public List<${table.entityName}ListVO> getList(${table.entityName}ListDTO ${table.entityName?uncap_first}ListDTO) {
        // 查询数据源
        List<${table.entityName}> ${table.entityName?uncap_first}List = list(new LambdaQueryWrapper<${table.entityName}>()
              <#------------  BEGIN 字段循环遍历  ---------->
              <#list table.fields as field>
                <#if field.comment?contains('名称') || field.comment?contains('标题') || field.comment?contains('状态') || field.comment?contains('类型')>
                <#if field.comment!?length gt 0>
                // ${field.comment}
                </#if>
                <#if field.columnType == "INTEGER">
                .eq(StringUtils.isNotNull(${table.entityName?uncap_first}ListDTO.get${field.propertyName?cap_first}()) && ${table.entityName?uncap_first}ListDTO.get${field.propertyName?cap_first}() > 0, ${table.entityName}::get${field.propertyName?cap_first}, ${table.entityName?uncap_first}ListDTO.get${field.propertyName?cap_first}())
                <#elseif field.columnType == "STRING">
                .like(StringUtils.isNotEmpty(${table.entityName?uncap_first}ListDTO.get${field.propertyName?cap_first}()), ${table.entityName}::get${field.propertyName?cap_first}, ${table.entityName?uncap_first}ListDTO.get${field.propertyName?cap_first}())
                </#if>
                </#if>
              </#list>
              <#------------  END 字段循环遍历  ---------->
                .eq(${table.entityName}::getDelFlag, 0)
                .orderByAsc(${table.entityName}::getId));
        // 实例化VO列表
        List<${table.entityName}ListVO> ${table.entityName?uncap_first}ListVOList = new ArrayList<>();
        if (StringUtils.isNotEmpty(${table.entityName?uncap_first}List)) {
            // 遍历数据源
            for (${table.entityName} ${table.entityName?uncap_first} : ${table.entityName?uncap_first}List) {
                // 实例化VO对象
                ${table.entityName}ListVO ${table.entityName?uncap_first}ListVO = new ${table.entityName}ListVO();
                BeanUtils.copyProperties(${table.entityName?uncap_first}, ${table.entityName?uncap_first}ListVO);
            <#------------  BEGIN 字段循环遍历  ---------->
            <#list table.fields as field>
                <#if field.propertyName == 'cover' || field.propertyName == 'logo' || field.propertyName == 'image'>
                // ${field.comment}
                String ${field.propertyName} = ${table.entityName?uncap_first}ListVO.get${field.propertyName?cap_first}();
                if (StringUtils.isNotEmpty(${field.propertyName})) {
                    ${table.entityName?uncap_first}ListVO.set${field.propertyName?cap_first}(CommonUtils.getFileURL(${field.propertyName}));
                }
                </#if>
            </#list>
            <#------------  END 字段循环遍历  ---------->
                ${table.entityName?uncap_first}ListVOList.add(${table.entityName?uncap_first}ListVO);
            }
        }
        return ${table.entityName?uncap_first}ListVOList;
    }

	/**
     * 根据ID查询信息
     *
     * @param id ${table.comment!}ID
     * @return 返回结果
     */
    @Override
    public ${table.entityName} getInfo(Integer id) {
        ${table.entityName} ${table.entityName?uncap_first} = getById(id);
        if (StringUtils.isNull(${table.entityName?uncap_first}) || !${table.entityName?uncap_first}.getDelFlag().equals(0)) {
            return null;
        }
        return ${table.entityName?uncap_first};
    }

    /**
     * 根据ID查询详情
     *
     * @param id ${table.comment!}ID
     * @return 返回结果
     */
    @Override
    public ${table.entityName}InfoVO getDetail(Integer id) {
        ${table.entityName} ${table.entityName?uncap_first} = getInfo(id);
        if (StringUtils.isNull(${table.entityName?uncap_first})) {
            return null;
        }
        // 实例化VO
        ${table.entityName}InfoVO ${table.entityName?uncap_first}InfoVO = new ${table.entityName}InfoVO();
        BeanUtils.copyProperties(${table.entityName?uncap_first}, ${table.entityName?uncap_first}InfoVO);
    <#------------  BEGIN 字段循环遍历  ---------->
    <#list table.fields as field>
        <#if field.propertyName == 'cover' || field.propertyName == 'logo' || field.propertyName == 'image'>
        // ${field.comment}
        String ${field.propertyName} = ${table.entityName?uncap_first}InfoVO.get${field.propertyName?cap_first}();
        if (StringUtils.isNotEmpty(${field.propertyName})) {
            ${table.entityName?uncap_first}InfoVO.set${field.propertyName?cap_first}(CommonUtils.getFileURL(${field.propertyName}));
        }
        </#if>
    </#list>
    <#------------  END 字段循环遍历  ---------->
        return ${table.entityName?uncap_first}InfoVO;
    }

	/**
     * 添加${table.comment!}
     *
     * @param ${table.entityName?uncap_first}AddDTO 参数
     * @return 返回结果
     */
    @Override
    public R add(${table.entityName}AddDTO ${table.entityName?uncap_first}AddDTO) {
        // 实例化对象
        ${table.entityName} ${table.entityName?uncap_first} = new ${table.entityName}();
        // 属性拷贝
        BeanUtils.copyProperties(${table.entityName?uncap_first}AddDTO, ${table.entityName?uncap_first});
    <#------------  BEGIN 字段循环遍历  ---------->
    <#list table.fields as field>
        <#if field.propertyName == 'cover' || field.propertyName == 'logo' || field.propertyName == 'image'>
        // ${field.comment}
        String ${field.propertyName} = ${table.entityName?uncap_first}AddDTO.get${field.propertyName?cap_first}();
        if (StringUtils.isNotEmpty(${field.propertyName}) && ${field.propertyName}.contains(AppConfig.getDomain())) {
            ${table.entityName?uncap_first}.set${field.propertyName?cap_first}(${field.propertyName}.replaceAll(AppConfig.getDomain(), ""));
        }
        </#if>
    </#list>
    <#------------  END 字段循环遍历  ---------->
        boolean result = save(${table.entityName?uncap_first});
        if (!result) {
            return R.failed();
        }
        return R.ok();
    }

    /**
     * 更新${table.comment!}
     *
     * @param ${table.entityName?uncap_first}UpdateDTO 参数
     * @return 返回结果
     */
    @Override
    public R update(${table.entityName}UpdateDTO ${table.entityName?uncap_first}UpdateDTO) {
        // 根据ID查询信息
        ${table.entityName} ${table.entityName?uncap_first} = getInfo(${table.entityName?uncap_first}UpdateDTO.getId());
        if (StringUtils.isNull(${table.entityName?uncap_first})) {
            return R.failed("记录不存在");
        }
        // 属性拷贝
        BeanUtils.copyProperties(${table.entityName?uncap_first}UpdateDTO, ${table.entityName?uncap_first});
        <#------------  BEGIN 字段循环遍历  ---------->
    <#list table.fields as field>
        <#if field.propertyName == 'cover' || field.propertyName == 'logo' || field.propertyName == 'image'>
        // ${field.comment}
        String ${field.propertyName} = ${table.entityName?uncap_first}UpdateDTO.get${field.propertyName?cap_first}();
        if (StringUtils.isNotEmpty(${field.propertyName}) && ${field.propertyName}.contains(AppConfig.getDomain())) {
            ${table.entityName?uncap_first}.set${field.propertyName?cap_first}(${field.propertyName}.replaceAll(AppConfig.getDomain(), ""));
        }
        </#if>
    </#list>
    <#------------  END 字段循环遍历  ---------->
        boolean result = updateById(${table.entityName?uncap_first});
        if (!result) {
            return R.failed();
        }
        return R.ok();
    }

	/**
     * 删除${table.comment!}
     *
     * @param id ${table.comment!}ID
     * @return 返回结果
     */
    @Override
    public R delete(Integer id) {
        // 根据ID查询信息
        ${table.entityName} ${table.entityName?uncap_first} = getInfo(id);
        if (StringUtils.isNull(${table.entityName?uncap_first})) {
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
     * 批量删除${table.comment!}
     *
     * @param idList ${table.comment!}ID
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
</#if>