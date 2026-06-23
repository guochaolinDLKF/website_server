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

import com.xiaomayi.core.constant.CacheConstant;
import com.xiaomayi.core.utils.R;
import com.xiaomayi.core.utils.StringUtils;
import com.xiaomayi.system.dto.cache.CacheValueDTO;
import com.xiaomayi.system.service.CacheService;
import com.xiaomayi.system.vo.cache.CacheInfoVO;
import lombok.AllArgsConstructor;
import org.springframework.data.redis.connection.RedisServerCommands;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * <p>
 * 系统缓存 服务实现类
 * </p>
 *
 * @author 小蚂蚁云团队
 * @since 2024-06-11
 */
@Service
@AllArgsConstructor
public class CacheServiceImpl implements CacheService {

    /**
     * 注入redisTemplate
     */
    private final RedisTemplate<String, String> redisTemplate;

    /**
     * 自定义缓存列表
     */
    private final static List<CacheInfoVO> caches = new ArrayList<CacheInfoVO>();

    {
        caches.add(new CacheInfoVO(CacheConstant.LOGIN_TOKEN_KEY, "用户信息"));
        caches.add(new CacheInfoVO(CacheConstant.SYS_PARAM_KEY, "参数信息"));
        caches.add(new CacheInfoVO(CacheConstant.SYS_DICT_KEY, "数据字典"));
        caches.add(new CacheInfoVO(CacheConstant.CAPTCHA_CODE_KEY, "验证码"));
        caches.add(new CacheInfoVO(CacheConstant.REPEAT_SUBMIT_KEY, "防重提交"));
        caches.add(new CacheInfoVO(CacheConstant.RATE_LIMIT_KEY, "限流处理"));
        caches.add(new CacheInfoVO(CacheConstant.PASSWORD_RETRY_COUNT_KEY, "密码错误次数"));
    }

    /**
     * 获取缓存信息
     *
     * @return 返回结果
     */
    @Override
    public R getCacheInfo() {
        // 缓存属性信息
        Properties info = (Properties) redisTemplate.execute((RedisCallback<Object>) RedisServerCommands::info);
        // 缓存数据库大小
        Object dbSize = redisTemplate.execute((RedisCallback<Object>) RedisServerCommands::dbSize);
        // 缓存指令库
        Properties commandStats = (Properties) redisTemplate.execute((RedisCallback<Object>) connection -> connection.info("commandstats"));

        // 返回结果
        Map<String, Object> result = new HashMap<>(3);
        result.put("info", info);
        result.put("dbSize", dbSize);

        // 判空处理
        if (StringUtils.isNotNull(commandStats)) {
            // 获取属性名称
            Set<String> propertyNames = commandStats.stringPropertyNames();
            if (StringUtils.isNotEmpty(propertyNames)) {
                List<Map<String, String>> pieList = new ArrayList<>();
                for (String propertyName : propertyNames) {
                    Map<String, String> data = new HashMap<>();
                    String property = commandStats.getProperty(propertyName);
                    data.put("name", StringUtils.removeStart(propertyName, "cmdstat_"));
                    data.put("value", StringUtils.substringBetween(property, "calls=", ",usec"));
                    pieList.add(data);
                }
                result.put("commandStats", pieList);
            }
        }
        // 返回结果
        return R.ok(result);
    }

    /**
     * 获取缓存列表
     *
     * @return 返回结果
     */
    @Override
    public List<CacheInfoVO> getCacheNames() {
        return caches;
    }

    /**
     * 根据缓存名称获取KEY列表
     *
     * @param cacheName 缓存名称
     * @return 返回结果
     */
    @Override
    public R getCacheKeys(String cacheName) {
        Set<String> cacheKeys = redisTemplate.keys(cacheName + "*");
        return R.ok(cacheKeys);
    }

    /**
     * 获取缓存值
     *
     * @param cacheValueDTO 查询条件
     * @return 返回结果
     */
    @Override
    public R getCacheValue(CacheValueDTO cacheValueDTO) {
        // 缓存名称
        String cacheName = cacheValueDTO.getCacheName();
        // 缓存KEY
        String cacheKey = cacheValueDTO.getCacheKey();
        // 根据KEY获取缓存信息
        String cacheValue = redisTemplate.opsForValue().get(cacheKey);
        // 缓存对象VO
        CacheInfoVO cacheInfoVO = new CacheInfoVO(cacheName, cacheKey, cacheValue);
        // 返回结果
        return R.ok(cacheInfoVO);
    }

    /**
     * 根据名称删除缓存
     *
     * @param cacheName 缓存名称
     * @return 返回结果
     */
    @Override
    public R deleteCacheName(String cacheName) {
        Collection<String> cacheKeys = redisTemplate.keys(cacheName + "*");
        assert cacheKeys != null;
        redisTemplate.delete(cacheKeys);
        return R.ok();
    }

    /**
     * 根据缓存KEY删除缓存
     *
     * @param cacheKey 缓存kEY
     * @return 返回结果
     */
    @Override
    public R deleteCacheKey(String cacheKey) {
        redisTemplate.delete(cacheKey);
        return R.ok();
    }

    /**
     * 删除全部缓存
     *
     * @return 返回结果
     */
    @Override
    public R deleteCacheAll() {
        Collection<String> cacheKeys = redisTemplate.keys("*");
        assert cacheKeys != null;
        redisTemplate.delete(cacheKeys);
        return R.ok();
    }
}
