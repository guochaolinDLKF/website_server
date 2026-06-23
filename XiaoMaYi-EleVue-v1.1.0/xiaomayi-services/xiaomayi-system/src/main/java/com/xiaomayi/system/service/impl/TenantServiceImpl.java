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

import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiaomayi.core.config.AppConfig;
import com.xiaomayi.core.utils.CommonUtils;
import com.xiaomayi.core.utils.R;
import com.xiaomayi.core.utils.StringUtils;
import com.xiaomayi.system.dto.tenant.TenantAccountDTO;
import com.xiaomayi.system.dto.tenant.TenantAddDTO;
import com.xiaomayi.system.dto.tenant.TenantPageDTO;
import com.xiaomayi.system.dto.tenant.TenantUpdateDTO;
import com.xiaomayi.system.entity.Role;
import com.xiaomayi.system.entity.Tenant;
import com.xiaomayi.system.entity.User;
import com.xiaomayi.system.mapper.TenantMapper;
import com.xiaomayi.system.service.RoleService;
import com.xiaomayi.system.service.TenantService;
import com.xiaomayi.system.service.UserRoleService;
import com.xiaomayi.system.service.UserService;
import com.xiaomayi.system.utils.ParamResolver;
import com.xiaomayi.system.vo.tenant.TenantInfoVO;
import com.xiaomayi.system.vo.tenant.TenantListVO;
import com.xiaomayi.system.vo.user.UserInfoVO;
import com.xiaomayi.tenant.annotation.TenantIgnore;
import lombok.AllArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 租户 服务实现类
 * </p>
 *
 * @author 小蚂蚁云团队
 * @since 2024-07-05
 */
@Service
@AllArgsConstructor
public class TenantServiceImpl extends ServiceImpl<TenantMapper, Tenant> implements TenantService {

    private final UserService userService;
    private final RoleService roleService;
    private final UserRoleService userRoleService;

    /**
     * 查询分页列表
     *
     * @param tenantPageDTO 查询条件
     * @return 返回结果
     */
    @Override
    public Page<Tenant> page(TenantPageDTO tenantPageDTO) {
        // 分页设置
        Page<Tenant> page = new Page<>(tenantPageDTO.getPageNo(), tenantPageDTO.getPageSize());
        // 查询条件
        LambdaQueryWrapper<Tenant> wrapper = new LambdaQueryWrapper<Tenant>()
                // 租户名称
                .like(StringUtils.isNotEmpty(tenantPageDTO.getName()), Tenant::getName, tenantPageDTO.getName())
                // 租户状态：0-正常 1-禁用
                .like(StringUtils.isNotNull(tenantPageDTO.getStatus()), Tenant::getStatus, tenantPageDTO.getStatus())
                .eq(Tenant::getDelFlag, 0)
                .orderByAsc(Tenant::getId);
        // 查询分页数据
        Page<Tenant> pageData = page(page, wrapper);
        // 遍历数据源
        pageData.convert(item -> {
            // 实例化VO对象
            TenantListVO tenantListVO = new TenantListVO();
            BeanUtils.copyProperties(item, tenantListVO);
            // 租户图片
            String image = tenantListVO.getImage();
            if (StringUtils.isNotEmpty(image)) {
                tenantListVO.setImage(CommonUtils.getFileURL(image));
            }
            return tenantListVO;
        });
        // 返回结果
        return pageData;
    }

    /**
     * 根据ID查询信息
     *
     * @param id 租户ID
     * @return 返回结果
     */
    @Override
    public Tenant getInfo(Integer id) {
        Tenant tenant = getById(id);
        if (StringUtils.isNull(tenant) || !tenant.getDelFlag().equals(0)) {
            return null;
        }
        return tenant;
    }

    /**
     * 根据ID查询详情
     *
     * @param id 租户ID
     * @return 返回结果
     */
    @Override
    public TenantInfoVO getDetail(Integer id) {
        Tenant tenant = getInfo(id);
        if (StringUtils.isNull(tenant)) {
            return null;
        }
        // 实例化VO
        TenantInfoVO tenantInfoVO = new TenantInfoVO();
        BeanUtils.copyProperties(tenant, tenantInfoVO);
        // 租户图片
        String image = tenantInfoVO.getImage();
        if (StringUtils.isNotEmpty(image)) {
            tenantInfoVO.setImage(CommonUtils.getFileURL(image));
        }

        // 返回结果
        return tenantInfoVO;
    }

    /**
     * 添加租户
     *
     * @param tenantAddDTO 参数
     * @return 返回结果
     */
    @Override
    public R add(TenantAddDTO tenantAddDTO) {
        // 检查租户编码是否已存在
        if (checkExist(tenantAddDTO.getCode(), 0)) {
            return R.failed("租户编码已存在");
        }
        // 实例化对象
        Tenant tenant = new Tenant();
        // 属性拷贝
        BeanUtils.copyProperties(tenantAddDTO, tenant);
        // 租户图片
        String image = tenantAddDTO.getImage();
        if (StringUtils.isNotEmpty(image) && image.contains(AppConfig.getDomain())) {
            tenant.setImage(image.replaceAll(AppConfig.getDomain(), ""));
        }
        boolean result = save(tenant);
        if (!result) {
            return R.failed();
        }
        return R.ok();
    }

    /**
     * 更新租户
     *
     * @param tenantUpdateDTO 参数
     * @return 返回结果
     */
    @TenantIgnore
    @Override
    public R update(TenantUpdateDTO tenantUpdateDTO) {
        // 根据ID查询信息
        Tenant tenant = getInfo(tenantUpdateDTO.getId());
        if (StringUtils.isNull(tenant)) {
            return R.failed("记录不存在");
        }
        // 检查租户编码是否已存在
        if (checkExist(tenantUpdateDTO.getCode(), tenantUpdateDTO.getId())) {
            return R.failed("租户编码已存在");
        }
        // 属性拷贝
        BeanUtils.copyProperties(tenantUpdateDTO, tenant);
        // 租户图片
        String image = tenantUpdateDTO.getImage();
        if (StringUtils.isNotEmpty(image) && image.contains(AppConfig.getDomain())) {
            tenant.setImage(image.replaceAll(AppConfig.getDomain(), ""));
        }
        boolean result = updateById(tenant);
        if (!result) {
            return R.failed();
        }

        // 同步头像、邮箱、地址到用户信息
        UserInfoVO userInfoVO = userService.selectUserByUserName(tenant.getCode());
        if (StringUtils.isNotNull(userInfoVO)) {
            User user = new User();
            user.setId(userInfoVO.getId());
            user.setAvatar(tenant.getImage());
            user.setEmail(tenant.getContactEmail());
            user.setAddress(tenant.getContactAddress());
            boolean userRes = userService.updateById(user);
            if (!userRes) {
                return R.failed("同步信息失败");
            }
        }

        // 返回结果
        return R.ok();
    }

    /**
     * 删除租户
     *
     * @param id 租户ID
     * @return 返回结果
     */
    @Override
    public R delete(Integer id) {
        // 根据ID查询信息
        Tenant tenant = getInfo(id);
        if (StringUtils.isNull(tenant)) {
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
     * 批量删除租户
     *
     * @param idList 租户ID
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
     * 检查租户编码是否已存在
     *
     * @param code 租户编码
     * @param id   租户ID
     * @return 返回结果
     */
    @Override
    public boolean checkExist(String code, Integer id) {
        Tenant level = getOne(new LambdaQueryWrapper<Tenant>()
                .eq(Tenant::getCode, code)
                .ne(id > 0, Tenant::getId, id)
                .eq(Tenant::getDelFlag, 0), false);
        return StringUtils.isNotNull(level);
    }

    /**
     * 创建租户账号
     *
     * @param tenantAccountDTO 参数
     * @return 返回结果
     */
    @TenantIgnore
    @Override
    public R addAccount(TenantAccountDTO tenantAccountDTO) {
        // 查询租户信息
        Tenant tenant = getInfo(tenantAccountDTO.getTenantId());
        if (StringUtils.isNull(tenant)) {
            return R.failed("租户不存在");
        }

        // 获取租户默认角色编码
        String roleCode = ParamResolver.getParamValue("TENANT_DEFAULT_ROLE", "ROLE_TENANT");
        // 根据角色编码查询角色
        Role role = roleService.getInfoByCode(roleCode);
        if (StringUtils.isNull(role)) {
            return R.failed("角色不存在");
        }
        // 角色ID列表
        Integer[] roleIdList = new Integer[]{role.getId()};

        // 检查租户是否已开过账号
        User userInfo = userService.getOne(new LambdaQueryWrapper<User>()
                // 租户ID
                .eq(User::getTenantId, tenant.getId())
                // 是否主账号：1-是 0-否
                .eq(User::getMaster, 1)
                .eq(User::getDelFlag, 0), false);
        if (StringUtils.isNotNull(userInfo)) {
            return R.failed("此租户已开过账号");
        }
        // 创建租户总账号
        User user = new User();
        // 租户ID
        user.setTenantId(tenant.getId());
        // 租户名称
        user.setRealname(tenant.getName());
        // 用户性别：1-男 2-女 3-保密
        user.setGender(3);
        // 用户头像
        user.setAvatar(tenant.getImage());
        // 联系电话
        user.setMobile(tenant.getContactMobile());
        // 电子邮件
        user.setEmail(tenant.getContactEmail());
        // 用户类型：0-系统用户 1-租户用户
        user.setType(1);
        // 联系地址
        user.setAddress(tenant.getContactAddress());
        // 登录账号，此处采用租户唯一性编码
        user.setUsername(tenant.getCode());
//        // 加密盐
//        String salt = RandomUtil.randomString(10);
//        user.setSalt(salt);
//        // 获取租户默认密码参数
//        String tenantPassword = ParamResolver.getParamValue("TENANT_DEFAULT_PASSWORD", "123456");
//        // 设置密码
//        user.setPassword(SecurityUtils.encryptPassword(salt + tenantPassword));
        // 加密盐
        user.setSalt(tenantAccountDTO.getSalt());
        // 设置密码
        user.setPassword(tenantAccountDTO.getPassword());
        // 用户状态：1-正常 2-禁用
        user.setStatus(1);
        // 是否主账号
        user.setMaster(1);
        // 创建用户账号
        boolean result = userService.save(user);
        if (!result) {
            return R.failed("租户账号创建失败");
        }

        // 创建租户角色关系
        userRoleService.saveUserRole(user.getId(), roleIdList);

        // 返回结果
        return R.ok();
    }
}
