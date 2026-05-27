package com.ydzz.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Swagger安全配置类
 * 解决Swagger UI的Token验证问题
 * 
 * @author FortuneTelling
 * @since 1.0.0
 */
@Configuration
public class SwaggerSecurityConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("命理推算系统API文档")
                                                        .description("基于Spring Boot 3的命理推算系统，提供八字计算、命理分析等功能\n\n" +
                                "## 🔐 使用说明\n\n" +
                                "1. 首先使用 **Swagger认证** 分组下的登录接口获取Token\n" +
                                "2. 点击右上角的 **Authorize** 按钮配置Token\n" +
                                "3. 直接输入token值\n" +
                                "4. 然后就可以测试所有需要认证的接口了\n\n" +
                                "## 📋 测试账号\n\n" +
                                "- **管理员**: username: `xxx`, password: `xxx`\n" +
                                "- **普通用户**: username: `xxx`, password: `xxx`")
                        .version("1.0.0"))
                .components(new Components()
                        .addSecuritySchemes("Authorization", 
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("请输入Token\n\n获取Token步骤：\n1. 使用Swagger认证分组下的登录接口\n2. 复制返回的token值\n3. 在此处直接输入token值")
                        ))
                .addSecurityItem(new SecurityRequirement().addList("Authorization"));
    }
} 