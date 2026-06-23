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

import com.xiaomayi.core.constant.CacheConstant;
import com.xiaomayi.redis.core.RedisCache;
import com.xiaomayi.core.exception.BizException;
import com.xiaomayi.core.exception.user.UserPasswordNotMatchException;
import com.xiaomayi.security.utils.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * <p>
 * 密码服务实现
 * </p>
 *
 * @author 小蚂蚁云团队
 * @since 2024-05-21
 */
@Slf4j
@Component
public class PasswordService {

    @Autowired
    private RedisCache redisCache;

    /**
     * 密码错误重试最大次数
     */
    @Value(value = "${token.password.maxRetryCount}")
    private Integer maxRetryCount;

    /**
     * 账户登录锁定时间，默认10分钟
     */
    @Value(value = "${token.password.lockTime}")
    private Integer lockTime;

    /**
     * 获取登录密码错误次数缓存键
     *
     * @param username 登录用户名
     * @return 返回结果
     */
    private String getCacheKey(String username) {
        return CacheConstant.PASSWORD_RETRY_COUNT_KEY + username;
    }

    /**
     * 检测登录密码
     *
     * @param userPwd 用户密码
     */
    public void check(String salt, String userPwd) {
        // 获取安全认证对象
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        // 登录账号
        String username = authentication.getName();
        // 登录密码
        String password = authentication.getCredentials().toString();

        // 密码重试次数
        Integer retryCount = 0;
        // 获取密码错误重试次数缓存KEY
        String retryCacheKey = getCacheKey(username);
        if (redisCache.hasKey(retryCacheKey)) {
            retryCount = redisCache.getCacheObject(retryCacheKey);
        }
        // 判断重试次数是否超过设置的最大值
        if (retryCount >= maxRetryCount) {
            String retryMsg = String.format("密码错误重试次数已达最大值【%s】次，账号已锁定，【%d】分钟后自动解锁", maxRetryCount, lockTime);
            throw new BizException(retryMsg);
        }
        // 密码验证
        if (!SecurityUtils.matchesPassword(password, userPwd)) {
            // 密码错误计数器+1
            retryCount += 1;
            // 用户登录密码错误次数写入缓存
            redisCache.setCacheObject(retryCacheKey, retryCount, lockTime, TimeUnit.MINUTES);
            // 抛出异常
            throw new UserPasswordNotMatchException();
        } else {
            // 登录成功，清除登录失败次数缓存
            if (redisCache.hasKey(retryCacheKey)) {
                redisCache.deleteObject(retryCacheKey);
            }
        }
    }

}
