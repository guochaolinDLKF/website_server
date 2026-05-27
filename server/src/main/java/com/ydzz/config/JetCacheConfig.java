package com.ydzz.config;

import com.alicp.jetcache.anno.config.EnableCreateCacheAnnotation;
import com.alicp.jetcache.anno.config.EnableMethodCache;
import org.springframework.context.annotation.Configuration;

/**
 * JetCache 配置类
 * 
 * @author FortuneTelling
 * @since 1.0.0
 */
@Configuration
@EnableCreateCacheAnnotation
@EnableMethodCache(basePackages = "com.ydzz")
public class JetCacheConfig {
    
    // JetCache 配置由 application.yml 自动配置
    // 这里只是启用注解功能
} 