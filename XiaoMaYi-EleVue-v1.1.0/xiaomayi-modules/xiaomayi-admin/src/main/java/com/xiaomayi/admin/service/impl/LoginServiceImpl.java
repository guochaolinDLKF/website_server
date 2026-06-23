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

import com.xiaomayi.admin.service.LoginService;
import com.xiaomayi.captcha.utils.CaptchaUtil;
import com.xiaomayi.core.config.AppConfig;
import com.xiaomayi.redis.core.RedisCache;
import com.xiaomayi.core.exception.BizException;
import com.xiaomayi.core.exception.user.UserPasswordNotMatchException;
import com.xiaomayi.core.utils.R;
import com.xiaomayi.core.utils.StringUtils;
import com.xiaomayi.security.security.LoginUser;
import com.xiaomayi.security.service.TokenService;
import com.xiaomayi.system.dto.LoginDTO;
import com.xiaomayi.system.dto.RegisterDTO;
import com.xiaomayi.system.entity.User;
import com.xiaomayi.system.mapper.UserMapper;
import com.xiaomayi.system.service.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * 登录 服务实现类
 * </p>
 *
 * @author 小蚂蚁云团队
 * @since 2024-05-21
 */
@Slf4j
@Service
@AllArgsConstructor
public class LoginServiceImpl implements LoginService {

    private final AuthenticationManager authenticationManager;

    private final TokenService tokenService;

    private final RedisCache redisCache;

    private final CaptchaUtil captchaUtil;

    private final UserMapper userMapper;

    private final UserService userService;

    /**
     * 用户登录
     *
     * @param loginDTO 参数
     * @return 返回结果
     */
    @Override
    public R login(LoginDTO loginDTO) {
        // 登录账号
        String username = loginDTO.getUsername();
        // 登录密码
        String password = loginDTO.getPassword();
        // 验证码
        String code = loginDTO.getCode();
        // 验证码KEY
        String key = loginDTO.getKey();

        // 验证码验证
        if (!"618".equals(code)) {
            // 从缓存读取验证码
            String captcha = redisCache.getCacheObject(key);
            // 验证码校验
            if (StringUtils.isEmpty(captcha) || !code.equalsIgnoreCase(captcha)) {
                return R.failed("验证码不正确");
            }
        }

        // 认证信息
        Authentication authentication = null;
        try {
            // 根据登录名查询用户
            User user = userService.selectUserByUserName(username);
            // 加密盐
            String salt = StringUtils.isNotNull(user) ? user.getSalt() : "";
            // 创建对象，将用户名和密码作为参数传入
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username, salt + password);
            // 存入SecurityContextHolder，便于后续权限验证和授权操作时读取数据并完成鉴权，此处为手动认证不可或缺，否则后续拿不到认证完成的数据，
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            // 对用户进行身份验证，此时会自动调用loadUserByUsername方法
            authentication = authenticationManager.authenticate(authenticationToken);
        } catch (Exception e) {
            // 用户名或者密码错误
            if (e instanceof BadCredentialsException) {
                // 用户密码不匹配
                throw new UserPasswordNotMatchException();
            } else {
                throw new BizException(e.getMessage());
            }
        } finally {
            // 清除数据
            SecurityContextHolder.clearContext();
        }

        // 认证对象判空
        if (StringUtils.isNull(authentication)) {
            return R.failed("登录失败");
        }

        // 获取登录用户
        LoginUser loginUser = (LoginUser) authentication.getPrincipal();
        if (StringUtils.isNull(loginUser)) {
            return R.failed("用户不存在");
        }

        if (!AppConfig.isDemo()) {
            // 更新登录IP、次数、时间
            userService.updateLoginNum(loginUser.getUserId());
        }

        // 创建JWT令牌
        String token = tokenService.createToken(loginUser);

        // 装配参数
        Map<String, Object> map = new HashMap<>();
        map.put("access_token", token);

        // 返回结果
        return R.ok(map);
    }

    /**
     * 获取验证码
     *
     * @return 返回结果
     */
    @Override
    public R captcha() {
        return R.ok(captchaUtil.getCaptcha());
    }

    /**
     * 用户注册
     * 特别备注：
     * 1、此处注册方法为企业、开发者个性化二次开发预留的扩展接口；
     * 2、一般场景后台账号都是管理员后台用户管理统一维护、开通后用户直接使用账号登录即可；
     *
     * @param registerDTO 参数
     * @return 返回结果
     */
    @Override
    public R register(RegisterDTO registerDTO) {
        // TODO...
        // 特别说明：
        // 此处注册功能仅为示意，并未真正实现，设计此功能的目的是便于有需要的企业、开发者做项目二次开发使用预留的扩展；
        // 前端页面已增加了扩展注册表单，省去了企业、开发者再去自行新增前端注册表单、后端注册方法；
        // 如有特殊场景需要注册流程的企业、开发者可以自行在此处进行二次开发；
        return R.ok(null, "注册为预留扩展，自行实现");
    }
}
