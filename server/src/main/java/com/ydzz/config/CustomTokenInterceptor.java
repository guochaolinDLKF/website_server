package com.ydzz.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import cn.dev33.satoken.interceptor.SaInterceptor;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaTokenConsts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 自定义Token拦截器
 * 在Sa-Token认证前介入，对前端传递的加密Token进行解密处理
 * 
 * @author FortuneTelling
 * @since 1.0.0
 */
@Component
public class CustomTokenInterceptor implements HandlerInterceptor {

    private static final Logger log = LoggerFactory.getLogger(CustomTokenInterceptor.class);

    @Autowired
    private CustomTokenParser tokenParser;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {

        log.debug("CustomTokenInterceptor开始处理请求: {} {}", request.getMethod(), request.getRequestURI());

        // 从请求头中获取Token
        String encryptedToken = request.getHeader("Authorization");
        if (encryptedToken == null || encryptedToken.isEmpty()) {
            log.debug("Authorization header为空，尝试从参数中获取token");
            // 尝试从参数中获取
            encryptedToken = request.getParameter("token");
        }

        if (encryptedToken != null && !encryptedToken.isEmpty()) {
            log.debug("检测到Token，长度: {}", encryptedToken.length());
            
            try {
                // 解密Token
                String originalToken = tokenParser.decryptToken(encryptedToken);
                
                if (originalToken != null && !originalToken.isEmpty()) {
                    log.debug("Token解密成功，解密后长度: {}", originalToken.length());

                // 将解密后的Token设置到请求属性中，供Sa-Token使用
                request.setAttribute(SaTokenConsts.JUST_CREATED, originalToken);

                // 手动设置当前Token上下文（关键步骤）
                StpUtil.setTokenValue(originalToken);
                    
                    log.debug("Token已设置到Sa-Token上下文");
                } else {
                    log.warn("Token解密后为空");
                }

            } catch (Exception e) {
                log.error("Token解密失败: {}", e.getMessage(), e);
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token解密失败: " + e.getMessage());
                return false;
            }
        } else {
            log.debug("未检测到Token，继续执行Sa-Token原有逻辑");
        }

        // 继续执行Sa-Token的原有逻辑
        try {
        SaInterceptor interceptor = new SaInterceptor();
            boolean result = interceptor.preHandle(request, response, handler);
            log.debug("Sa-Token拦截器处理完成，结果: {}", result);
            return result;
        } catch (Exception e) {
            log.error("Sa-Token拦截器处理异常: {}", e.getMessage(), e);
            throw e;
        }
    }
}
