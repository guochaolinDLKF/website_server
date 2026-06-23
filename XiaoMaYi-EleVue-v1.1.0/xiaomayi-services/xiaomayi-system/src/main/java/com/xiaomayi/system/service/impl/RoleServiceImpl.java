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
import com.xiaomayi.core.utils.R;
import com.xiaomayi.core.utils.StringUtils;
import com.xiaomayi.system.dto.role.RoleAddDTO;
import com.xiaomayi.system.dto.role.RoleListDTO;
import com.xiaomayi.system.dto.role.RolePageDTO;
import com.xiaomayi.system.dto.role.RoleUpdateDTO;
import com.xiaomayi.system.entity.Role;
import com.xiaomayi.system.mapper.RoleMapper;
import com.xiaomayi.system.service.RoleService;
import com.xiaomayi.system.vo.role.RoleInfoVO;
import com.xiaomayi.system.vo.role.RoleListVO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 角色 服务实现类
 * </p>
 *
 * @author 小蚂蚁云团队
 * @since 2024-03-23
 */
@Service
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements RoleService {

    /**
     * 查询分页列表
     *
     * @param rolePageDTO 查询条件
     * @return 返回结果
     */
    @Override
    public Page<Role> page(RolePageDTO rolePageDTO) {
        // 分页设置
        Page<Role> page = new Page<>(rolePageDTO.getPageNo(), rolePageDTO.getPageSize());
        // 查询条件
        LambdaQueryWrapper<Role> wrapper = new LambdaQueryWrapper<Role>()
                // 角色名称
                .like(StringUtils.isNotEmpty(rolePageDTO.getName()), Role::getName, rolePageDTO.getName())
                .eq(Role::getDelFlag, 0)
                .orderByAsc(Role::getId);
        // 查询分页数据
        Page<Role> pageData = page(page, wrapper);
        // 遍历数据源
        pageData.convert(item -> {
            // 实例化VO对象
            RoleListVO roleListVO = new RoleListVO();
            BeanUtils.copyProperties(item, roleListVO);
            return roleListVO;
        });
        // 返回结果
        return pageData;
    }

    /**
     * 查询数据列表
     *
     * @param roleListDTO 查询条件
     * @return 返回结果
     */
    @Override
    public List<RoleListVO> getList(RoleListDTO roleListDTO) {
        // 查询数据源
        List<Role> roleList = list(new LambdaQueryWrapper<Role>()
                // 角色名称
                .like(StringUtils.isNotEmpty(roleListDTO.getName()), Role::getName, roleListDTO.getName())
                .eq(Role::getDelFlag, 0)
                .orderByAsc(Role::getId));
        // 实例化VO列表
        List<RoleListVO> roleListVOList = new ArrayList<>();
        if (StringUtils.isNotEmpty(roleList)) {
            // 遍历数据源
            for (Role role : roleList) {
                // 实例化VO对象
                RoleListVO roleListVO = new RoleListVO();
                BeanUtils.copyProperties(role, roleListVO);
                roleListVOList.add(roleListVO);
            }
        }
        return roleListVOList;
    }

    /**
     * 根据ID查询信息
     *
     * @param id 角色ID
     * @return 返回结果
     */
    @Override
    public Role getInfo(Integer id) {
        Role role = getById(id);
        if (StringUtils.isNull(role) || !role.getDelFlag().equals(0)) {
            return null;
        }
        return role;
    }

    /**
     * 根据ID查询详情
     *
     * @param id 角色ID
     * @return 返回结果
     */
    @Override
    public RoleInfoVO getDetail(Integer id) {
        Role role = getInfo(id);
        if (StringUtils.isNull(role)) {
            return null;
        }
        // 实例化VO
        RoleInfoVO roleInfoVO = new RoleInfoVO();
        BeanUtils.copyProperties(role, roleInfoVO);
        return roleInfoVO;
    }

    /**
     * 添加角色
     *
     * @param roleAddDTO 参数
     * @return 返回结果
     */
    @Override
    public R add(RoleAddDTO roleAddDTO) {
        // 检查角色编码是否已存在
        if (checkExist(roleAddDTO.getCode(), 0)) {
            return R.failed("角色编码已存在");
        }
        // 实例化对象
        Role role = new Role();
        // 属性拷贝
        BeanUtils.copyProperties(roleAddDTO, role);
        boolean result = save(role);
        if (!result) {
            return R.failed();
        }
        return R.ok();
    }

    /**
     * 更新角色
     *
     * @param roleUpdateDTO 参数
     * @return 返回结果
     */
    @Override
    public R update(RoleUpdateDTO roleUpdateDTO) {
        // 根据ID查询信息
        Role role = getInfo(roleUpdateDTO.getId());
        if (StringUtils.isNull(role)) {
            return R.failed("记录不存在");
        }
        // 检查角色编码是否已存在
        if (checkExist(roleUpdateDTO.getCode(), role.getId())) {
            return R.failed("角色编码已存在");
        }
        // 属性拷贝
        BeanUtils.copyProperties(roleUpdateDTO, role);
        boolean result = updateById(role);
        if (!result) {
            return R.failed();
        }
        return R.ok();
    }

    /**
     * 删除角色
     *
     * @param idList 角色ID
     * @return 返回结果
     */
    @Override
    public R delete(List<Integer> idList) {
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
     * 检查角色编码是否已存在
     *
     * @param code 角色编码
     * @param id   角色ID
     * @return 返回结果
     */
    @Override
    public boolean checkExist(String code, Integer id) {
        Role role = getOne(new LambdaQueryWrapper<Role>()
                .eq(Role::getCode, code)
                .ne(id > 0, Role::getId, id)
                .eq(Role::getDelFlag, 0), false);
        return StringUtils.isNotNull(role);
    }

    /**
     * 根据编码获取角色
     *
     * @param code 角色编码
     * @return 返回结果
     */
    @Override
    public Role getInfoByCode(String code) {
        Role role = getOne(new LambdaQueryWrapper<Role>()
                .eq(Role::getCode, code)
                .eq(Role::getDelFlag, 0), false);
        return role;
    }
}
