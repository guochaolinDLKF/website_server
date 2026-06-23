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

package com.xiaomayi.admin.service.impl;

import com.xiaomayi.core.exception.user.UserAccountBlockedException;
import com.xiaomayi.core.exception.user.UserAccountDeleteException;
import com.xiaomayi.core.exception.user.UserNotExistException;
import com.xiaomayi.core.utils.StringUtils;
import com.xiaomayi.security.security.LoginUser;
import com.xiaomayi.security.vo.UserVO;
import com.xiaomayi.system.enums.UserStatusEnum;
import com.xiaomayi.system.mapper.MenuMapper;
import com.xiaomayi.system.service.UserService;
import com.xiaomayi.system.vo.user.UserInfoVO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 安全认证登录实现
 * </p>
 *
 * @author 小蚂蚁云团队
 * @since 2024-05-21
 */
@Slf4j
@Service
@AllArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final PasswordService passwordService;
    private final UserService userService;
    private final MenuMapper menuMapper;

    /**
     * 根据用户名查询用户
     *
     * @param username 用户名
     * @return 返回结果
     * @throws UsernameNotFoundException 异常处理
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 根据用户名查询用户
        UserInfoVO user = userService.selectUserByUserName(username);
        if (StringUtils.isNull(user)) {
            log.info("登录用户：{} 不存在.", username);
            throw new UserNotExistException();
        } else if (!user.getDelFlag().equals(0)) {
            log.info("登录用户：{} 已被删除.", username);
            throw new UserAccountDeleteException();
        } else if (!UserStatusEnum.OK.getCode().equals(user.getStatus())) {
            log.info("登录用户：{} 已被锁定禁用.", username);
            throw new UserAccountBlockedException();
        }

        // 检测登录密码
        passwordService.check(user.getSalt(), user.getPassword());

        // 查询用户的权限列表
        List<String> permissions = new ArrayList<>();
        // 用户判断
        if (user.getId().equals(1)) {
            // 超级管理员，全部放行
            permissions.add("*:*:*");
        } else {
            // 非超管人员
            permissions = menuMapper.getPermissions(user.getId());
        }

        // 查询用户角色权限列表
        List<String> roles = new ArrayList<>();
        roles.add("ROLE_ADMIN");

        // 实例化登录用户VO
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);

        // 返回登录用户
        return new LoginUser(userVO, permissions, roles);
    }
}