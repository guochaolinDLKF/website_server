package com.ydzz.config;

import cn.dev33.satoken.stp.StpInterface;
import com.ydzz.admin.config.AdminStpUtil;
import com.ydzz.admin.service.AdminRbacService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Sa-Token配置类
 * 负责权限验证接口配置
 *
 * <p>支持两个登录域：C 端用户（默认 loginType）保持原有逻辑；
 * 后台管理端（loginType = "admin"）从 RBAC 表加载权限/角色。</p>
 *
 * @author FortuneTelling
 * @since 1.0.0
 */
@Configuration
public class SaTokenConfig {

    private static final Logger log = LoggerFactory.getLogger(SaTokenConfig.class);

    /**
     * 自定义权限验证接口（C 端 + 后台两个登录域）
     *
     * @param adminRbacService 后台 RBAC 查询服务（懒加载，避免启动期循环依赖）
     */
    @Bean
    public StpInterface stpInterface(@Lazy AdminRbacService adminRbacService) {
        return new StpInterface() {
            @Override
            public List<String> getPermissionList(Object loginId, String loginType) {
                // 后台管理端：从 RBAC 表加载真实权限码
                if (AdminStpUtil.TYPE.equals(loginType)) {
                    return adminRbacService.listPermissionCodes(Long.valueOf(loginId.toString()));
                }
                // C 端用户：保持原有逻辑
                return List.of("user:read", "user:write");
            }

            @Override
            public List<String> getRoleList(Object loginId, String loginType) {
                // 后台管理端：从 RBAC 表加载真实角色码
                if (AdminStpUtil.TYPE.equals(loginType)) {
                    return adminRbacService.listRoleCodes(Long.valueOf(loginId.toString()));
                }
                // C 端用户：保持原有逻辑
                if ("1".equals(loginId.toString())) {
                    return List.of("ADMIN");
                }
                return List.of("USER");
            }
        };
    }
} 