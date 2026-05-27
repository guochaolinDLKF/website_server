package com.ydzz.config;

import cn.dev33.satoken.stp.StpInterface;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.strategy.SaStrategy;
import cn.dev33.satoken.util.SaTokenConsts;
import org.springframework.stereotype.Component;
import cn.dev33.satoken.secure.SaSecureUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 自定义Token解析器
 * 负责Token的加密和解密，支持Beaver前缀
 * 
 * @author FortuneTelling
 * @since 1.0.0
 */
@Component
public class CustomTokenParser {

    private static final Logger log = LoggerFactory.getLogger(CustomTokenParser.class);
    
    // 保持Beaver前缀不变
    private static final String TOKEN_PREFIX = "Bearer ";
    
    // 使用与SaTokenConfig一致的AES密钥
    private static final String AES_KEY = "FortuneTelling88";

    /**
     * 解密Token方法
     * @param encryptedToken 加密的Token（可能包含Beaver前缀）
     * @return 解密后的原始Token
     */
    public String decryptToken(String encryptedToken) {
        if (encryptedToken == null || encryptedToken.isEmpty()) {
            log.debug("Token为空，无法解密");
            return null;
        }
        
        try {
            String tokenToDecrypt = encryptedToken;
            
            // 检查是否包含Beaver前缀
            if (encryptedToken.startsWith(TOKEN_PREFIX)) {
                tokenToDecrypt = encryptedToken.substring(TOKEN_PREFIX.length());
                log.debug("检测到Beaver前缀，去除前缀后Token长度: {}", tokenToDecrypt.length());
            } else {
                log.debug("未检测到Beaver前缀，直接处理Token，长度: {}", encryptedToken.length());
            }

            // 使用AES解密
            String originalToken = SaSecureUtil.aesDecrypt(AES_KEY, tokenToDecrypt);
            log.debug("Token解密成功，解密后长度: {}", originalToken != null ? originalToken.length() : 0);
            
            return originalToken;
        } catch (Exception e) {
            log.error("Token解密失败: {}", e.getMessage(), e);
            throw new RuntimeException("Token解密失败: " + e.getMessage(), e);
        }
    }

    /**
     * 创建加密Token（用于登录响应）
     * @param originalToken 原始Token
     * @return 加密后的Token（包含Beaver前缀）
     */
    public String encryptToken(String originalToken) {
        if (originalToken == null || originalToken.isEmpty()) {
            log.debug("原始Token为空，无法加密");
            return null;
        }
        
        try {
            // 使用AES加密
            String encryptedToken = SaSecureUtil.aesEncrypt(AES_KEY, originalToken);
            String result = encryptedToken;
            
            log.debug("Token加密成功，原始长度: {}, 加密后长度: {}",originalToken.length(), result.length());
            
            return result;
        } catch (Exception e) {
            log.error("Token加密失败: {}", e.getMessage(), e);
            throw new RuntimeException("Token加密失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 判断Token是否已加密
     * @param token Token字符串
     * @return 是否包含Beaver前缀
     */
    public boolean isEncrypted(String token) {
        return token != null && token.startsWith(TOKEN_PREFIX);
    }
}