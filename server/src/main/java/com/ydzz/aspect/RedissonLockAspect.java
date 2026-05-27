package com.ydzz.aspect;

import com.ydzz.annotation.RedissonLock;
import com.ydzz.common.ErrorCode;
import com.ydzz.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

/**
 * Redisson分布式锁切面
 * 
 * @author FortuneTelling
 * @since 1.0.0
 */
@Slf4j
@Aspect
@Component
public class RedissonLockAspect {
    
    @Autowired
    private RedissonClient redissonClient;
    
    private final ExpressionParser parser = new SpelExpressionParser();
    
    private final DefaultParameterNameDiscoverer nameDiscoverer = new DefaultParameterNameDiscoverer();
    
    @Around("@annotation(redissonLock)")
    public Object around(ProceedingJoinPoint joinPoint, RedissonLock redissonLock) throws Throwable {
        // 1. 解析锁的key
        String lockKey = parseKey(redissonLock.key(), joinPoint);
        
        // 2. 获取锁
        RLock lock = redissonClient.getLock(lockKey);
        
        try {
            // 3. 尝试获取锁
            boolean acquired = lock.tryLock(redissonLock.waitTime(), redissonLock.leaseTime(), TimeUnit.SECONDS);
            
            if (!acquired) {
                log.warn("获取分布式锁失败，key: {}", lockKey);
                throw new BusinessException(ErrorCode.TOO_MANY_REQUESTS, redissonLock.message());
            }
            
            log.debug("获取分布式锁成功，key: {}", lockKey);
            
            // 4. 执行业务逻辑
            return joinPoint.proceed();
            
        } catch (InterruptedException e) {
            log.error("获取分布式锁被中断，key: {}", lockKey, e);
            Thread.currentThread().interrupt();
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "获取锁被中断");
        } catch (Exception e) {
            log.error("执行业务逻辑异常，key: {}", lockKey, e);
            throw e;
        } finally {
            // 5. 释放锁
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
                log.debug("释放分布式锁成功，key: {}", lockKey);
            }
        }
    }
    
    /**
     * 解析SpEL表达式获取锁的key
     */
    private String parseKey(String keyExpression, ProceedingJoinPoint joinPoint) {
        try {
            // 获取方法签名
            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            Method method = signature.getMethod();
            
            // 获取参数名
            String[] paramNames = nameDiscoverer.getParameterNames(method);
            Object[] args = joinPoint.getArgs();
            
            // 创建SpEL上下文
            EvaluationContext context = new StandardEvaluationContext();
            if (paramNames != null) {
                for (int i = 0; i < paramNames.length; i++) {
                    context.setVariable(paramNames[i], args[i]);
                }
            }
            
            // 解析表达式
            Expression expression = parser.parseExpression(keyExpression);
            Object value = expression.getValue(context);
            
            return value != null ? value.toString() : keyExpression;
            
        } catch (Exception e) {
            log.warn("解析SpEL表达式失败，使用原始key: {}", keyExpression, e);
            return keyExpression;
        }
    }
}
