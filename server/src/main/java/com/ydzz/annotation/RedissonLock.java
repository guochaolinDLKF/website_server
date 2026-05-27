package com.ydzz.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Redisson分布式锁注解
 * 
 * 使用示例：
 * @RedissonLock(key = "order:create:#{#request.userId}", waitTime = 30, leaseTime = 60)
 * public OrderResponse createOrder(CreateOrderRequest request) {
 *     // 业务逻辑
 * }
 * 
 * @author FortuneTelling
 * @since 1.0.0
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RedissonLock {
    
    /**
     * 锁的key，支持SpEL表达式
     * 示例：
     * - "order:create:#{#request.userId}" - 使用请求参数中的userId
     * - "order:create:#{#userId}" - 使用方法参数中的userId
     * - "order:create:fixed" - 固定key
     */
    String key();
    
    /**
     * 等待获取锁的时间（秒）
     * 默认30秒
     */
    int waitTime() default 30;
    
    /**
     * 锁的持有时间（秒）
     * 默认60秒
     * 如果设置为-1，则使用看门狗机制自动续期
     */
    int leaseTime() default 60;
    
    /**
     * 获取锁失败时的错误消息
     * 默认"系统繁忙，请稍后重试"
     */
    String message() default "系统繁忙，请稍后重试";
}








