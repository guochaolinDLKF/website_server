package com.ydzz.config;

import cn.dev33.satoken.stp.StpInterface;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Sa-Token配置类
 * 负责权限验证接口配置
 * 
 * @author FortuneTelling
 * @since 1.0.0
 */
@Configuration
public class SaTokenConfig {

    private static final Logger log = LoggerFactory.getLogger(SaTokenConfig.class);

    /**
     * 自定义权限验证接口
     */
    @Bean
    public StpInterface stpInterface() {
        return new StpInterface() {
            @Override
            public List<String> getPermissionList(Object loginId, String loginType) {
                // 返回用户权限列表
                return List.of("user:read", "user:write");
            }

            @Override
            public List<String> getRoleList(Object loginId, String loginType) {
                // 返回用户角色列表
                if ("1".equals(loginId.toString())) {
                    return List.of("ADMIN");
                }
                return List.of("USER");
            }
        };
    }
} 