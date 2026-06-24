package com.ydzz.admin.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.resource.PathResourceResolver;

import java.io.IOException;

/**
 * 后台前端 SPA 静态资源与路由回退配置。
 *
 * <p>后台前端（admin-web）以单页应用形式打包进 jar，静态资源位于 {@code classpath:/static/admin/}，
 * 访问入口为 {@code /admin/}。为支持 Vue Router 的 history 模式：</p>
 * <ul>
 *   <li>{@code /admin/assets/xxx.js} 等真实文件 → 由静态资源处理器直接返回；</li>
 *   <li>{@code /admin/dashboard} 等前端路由（无对应文件）→ 回退到 {@code index.html} 交前端路由处理；</li>
 *   <li>{@code /admin}（无尾斜杠）→ 重定向到 {@code /admin/}。</li>
 * </ul>
 *
 * <p>使用 {@link PathResourceResolver} 回退，避免在 Spring Boot 3 的 PathPattern 解析器下
 * 使用非法的 {@code **} 通配映射。未打包前端资源（未走 {@code -Pwith-frontend}）时，
 * 该路径请求返回 404，不影响后端接口与启动。</p>
 *
 * @author WebsiteServer
 * @since 1.0.0
 */
@Configuration
public class AdminWebMvcConfig implements WebMvcConfigurer {

    private static final String LOCATION = "classpath:/static/admin/";
    private static final ClassPathResource INDEX = new ClassPathResource("static/admin/index.html");

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        // /admin -> /admin/（保证相对资源路径正确）
        registry.addRedirectViewController("/admin", "/admin/");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/admin/**")
                .addResourceLocations(LOCATION)
                .resourceChain(true)
                .addResolver(new PathResourceResolver() {
                    @Override
                    protected Resource getResource(String resourcePath, Resource location) throws IOException {
                        if (resourcePath != null && !resourcePath.isEmpty()) {
                            Resource requested = location.createRelative(resourcePath);
                            if (requested.exists() && requested.isReadable()) {
                                return requested;
                            }
                        }
                        // SPA 回退：非真实文件统一返回入口 index.html（不存在时返回 null -> 404）
                        return INDEX.exists() ? INDEX : null;
                    }
                });
    }
}
