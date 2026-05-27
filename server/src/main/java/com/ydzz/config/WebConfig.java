package com.ydzz.config;

import cn.dev33.satoken.interceptor.SaInterceptor;
import cn.dev33.satoken.router.SaRouter;
import cn.dev33.satoken.stp.StpUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;

/**
 * Web配置类
 * 负责注册拦截器，定义认证规则
 *
 * @author FortuneTelling
 * @since 1.0.0
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    private static final Logger log = LoggerFactory.getLogger(WebConfig.class);

    @Autowired
    private CustomTokenInterceptor customTokenInterceptor;

    @Autowired
    private CurrentUserArgumentResolver currentUserArgumentResolver;

    @Value("${sa-token.timeout:2592000}")
    private Long timeout;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        log.info("开始注册Web拦截器");

        // 第一步：注册自定义Token拦截器（优先级最高）
        // 负责在Sa-Token认证前介入，解密前端传递的加密Token
        registry.addInterceptor(customTokenInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns(
                    "/api/user/login",
                    "/swagger-ui/**",
                    "/swagger-ui.html",
                    "/v3/api-docs/**",
                    "/error",
                    "/favicon.ico"
                )
                .order(1);

        log.info("自定义Token拦截器注册完成，优先级: 1");

        // 第二步：注册Sa-Token拦截器
        registry.addInterceptor(new SaInterceptor(handler -> {
            log.debug("Sa-Token拦截器开始认证检查");

            SaRouter.match("/**")
                    .notMatch(
                        "/api/user/login",
                        "/swagger-ui/**",
                        "/swagger-ui.html",
                        "/v3/api-docs/**",
                        "/v3/api-docs",
                        "/swagger-resources/**",
                        "/webjars/**",
                        "/doc.html",
                        "/favicon.ico",
                        "/error"
                    )
                    .check(r -> {
                        log.debug("开始检查用户登录状态");
                        StpUtil.checkLogin();
                        if (StpUtil.isLogin()) {
                            StpUtil.renewTimeout(timeout);
                        }
                        log.debug("用户登录状态检查通过");
                    });

            log.debug("Sa-Token拦截器认证检查完成");
        }))
        .addPathPatterns("/**")
        .order(2);

        log.info("Web拦截器注册完成，总计: 2个拦截器");
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(currentUserArgumentResolver);
    }
}
