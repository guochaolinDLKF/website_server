package com.xiaomayi.admin.controller.demo;

import com.xiaomayi.redis.core.RedisCache;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

/**
 * <p>
 * 案例测试 前端控制器
 * </p>
 *
 * @author 小蚂蚁云团队
 * @since 2024-05-26
 */
@Slf4j
@RestController
@RequestMapping("/example")
@AllArgsConstructor
public class RedisController {

    @Autowired
    private RedisCache redisCache;

    /**
     * 缓存功能测试
     */
    @GetMapping("/test")
    public void test() {
        // 1. 设置缓存
        redisCache.setCacheObject("value", "123456");
        // 2. 设置缓存,有效期5分钟
        redisCache.setCacheObject("value", "123456", 5, TimeUnit.MINUTES);
        // 获取缓存
        String value = redisCache.getCacheObject("value");
        System.out.println(value);
        // 删除缓存
        redisCache.deleteObject("value");
    }

}
