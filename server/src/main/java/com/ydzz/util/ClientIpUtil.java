package com.ydzz.util;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

/**
 * 客户端 IP 解析工具
 * 从 HTTP 请求中获取客户端的真实外网 IP，支持代理穿透
 *
 * @author WebsiteServer
 * @since 1.0.0
 */
@Slf4j
public class ClientIpUtil {

    private static final String LOCALHOST_IPV4 = "127.0.0.1";
    private static final String LOCALHOST_IPV6 = "0:0:0:0:0:0:0:1";
    private static final String UNKNOWN = "unknown";

    private ClientIpUtil() {
        // 工具类，禁止实例化
    }

    /**
     * 从 HTTP 请求中获取客户端的真实外网 IP
     * 
     * 解析优先级：
     * 1. X-Forwarded-For（代理转发链，取第一个非内网 IP）
     * 2. X-Real-IP（Nginx 等反向代理设置的真实 IP）
     * 3. Proxy-Client-IP（Apache 等代理）
     * 4. WL-Proxy-Client-IP（WebLogic 代理）
     * 5. request.getRemoteAddr()（直连时的对端 IP）
     *
     * @param request HTTP 请求
     * @return 客户端真实 IP，本地开发环境返回 127.0.0.1
     */
    public static String getClientIp(HttpServletRequest request) {
        String ip = resolveFromHeaders(request);
        if (!StringUtils.hasText(ip)) {
            ip = request.getRemoteAddr();
        }
        return normalize(ip);
    }

    /**
     * 逐级尝试从代理请求头中解析真实 IP
     */
    private static String resolveFromHeaders(HttpServletRequest request) {
        // 1. X-Forwarded-For：格式为 "client, proxy1, proxy2"，取第一个
        String ip = extractFirstIp(request.getHeader("X-Forwarded-For"));
        if (StringUtils.hasText(ip)) return ip;

        // 2. X-Real-IP
        ip = request.getHeader("X-Real-IP");
        if (isValid(ip)) return ip;

        // 3. Proxy-Client-IP
        ip = request.getHeader("Proxy-Client-IP");
        if (isValid(ip)) return ip;

        // 4. WL-Proxy-Client-IP
        ip = request.getHeader("WL-Proxy-Client-IP");
        if (isValid(ip)) return ip;

        return null;
    }

    /**
     * 从 X-Forwarded-For 中提取第一个 IP（最原始的客户端 IP）
     */
    private static String extractFirstIp(String forwardedFor) {
        if (!isValid(forwardedFor)) return null;
        int index = forwardedFor.indexOf(',');
        return index > 0 ? forwardedFor.substring(0, index).trim() : forwardedFor.trim();
    }

    /**
     * IP 值是否有效（非空且非 unknown）
     */
    private static boolean isValid(String ip) {
        return StringUtils.hasText(ip) && !UNKNOWN.equalsIgnoreCase(ip);
    }

    /**
     * 规范化 IP：本地回环地址保持不变（ip-api 会返回本机归属地信息）
     */
    private static String normalize(String ip) {
        if (LOCALHOST_IPV4.equals(ip) || LOCALHOST_IPV6.equals(ip)) {
            log.debug("检测到本机回环地址: {}", ip);
        }
        return ip;
    }
}
