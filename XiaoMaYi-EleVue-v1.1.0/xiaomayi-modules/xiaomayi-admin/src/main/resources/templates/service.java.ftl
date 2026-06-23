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

package ${package.Service};

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xiaomayi.core.utils.R;
import ${packageName}.dto.${entity?lower_case}.${table.entityName}AddDTO;
import ${packageName}.dto.${entity?lower_case}.${table.entityName}ListDTO;
import ${packageName}.dto.${entity?lower_case}.${table.entityName}PageDTO;
import ${packageName}.dto.${entity?lower_case}.${table.entityName}UpdateDTO;
import ${package.Entity}.${entity};
import ${superServiceClassPackage};
import ${packageName}.vo.${entity?lower_case}.${table.entityName}InfoVO;
import ${packageName}.vo.${entity?lower_case}.${table.entityName}ListVO;

import java.util.List;

/**
 * <p>
 * ${table.comment!} 服务类
 * </p>
 *
 * @author ${author}
 * @since ${date}
 */
<#if kotlin>
interface ${table.serviceName} : ${superServiceClass}<${entity}>
<#else>
public interface ${table.serviceName} extends ${superServiceClass}<${entity}> {

    /**
     * 查询分页列表
     *
     * @param ${table.entityName?uncap_first}PageDTO 查询条件
     * @return 返回结果
     */
    Page<${table.entityName}> page(${table.entityName}PageDTO ${table.entityName?uncap_first}PageDTO);

	/**
     * 查询数据列表
     *
     * @param ${table.entityName?uncap_first}ListDTO 查询条件
     * @return 返回结果
     */
    List<${table.entityName}ListVO> getList(${table.entityName}ListDTO ${table.entityName?uncap_first}ListDTO);

	/**
     * 根据ID查询信息
     *
     * @param id ${table.comment!}ID
     * @return 返回结果
     */
    ${table.entityName} getInfo(Integer id);

    /**
     * 根据ID查询详情
     *
     * @param id ${table.comment!}ID
     * @return 返回结果
     */
    ${table.entityName}InfoVO getDetail(Integer id);

	/**
     * 添加${table.comment!}
     *
     * @param ${table.entityName?uncap_first}AddDTO 参数
     * @return 返回结果
     */
    R add(${table.entityName}AddDTO ${table.entityName?uncap_first}AddDTO);

    /**
     * 更新${table.comment!}
     *
     * @param ${table.entityName?uncap_first}UpdateDTO 参数
     * @return 返回结果
     */
    R update(${table.entityName}UpdateDTO ${table.entityName?uncap_first}UpdateDTO);

    /**
     * 删除${table.comment!}
     *
     * @param id ${table.comment!}ID
     * @return 返回结果
     */
    R delete(Integer id);

    /**
     * 批量删除${table.comment!}
     *
     * @param idList ${table.comment!}ID
     * @return 返回结果
     */
    R batchDelete(List<Integer> idList);
	
}
</#if>