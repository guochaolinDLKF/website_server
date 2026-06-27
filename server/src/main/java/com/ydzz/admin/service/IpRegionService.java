package com.ydzz.admin.service;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.ydzz.util.ClientIpUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * IP 外网地址解析 + 地区定位服务。
 *
 * <p>外网 IP：优先取请求中的公网 IP（X-Forwarded-For 等，生产环境即客户端公网 IP）；
 * 若为内网/回环（本地开发），调用 echo 服务取本机公网 IP（= 当前设备所在网络的外网 IP）。
 * 地区：高德 Web 服务 IP 定位接口（https://restapi.amap.com/v3/ip），仅覆盖国内 IP；
 * 国内返回省/市，国外返回空省份 → 判定为「国外」并标记异常。结果均做缓存以降低外部调用。</p>
 *
 * @author WebsiteServer
 * @since 1.0.0
 */
@Slf4j
@Service
public class IpRegionService {

    /** 高德 Web 服务 Key（IP 定位）。默认沿用 VisitInfoServiceImpl 中的 Key，可在 application-*.yml 用 amap.key 覆盖。 */
    @Value("${amap.key:a0aea882477a1b77b97eee0e8fc07a30}")
    private String amapKey;

    /** 获取本机公网 IP 的 echo 服务（返回纯文本 IP），用于本地开发场景。默认用 ip-api 的 line 格式（HTTP，避免部分环境 HTTPS 连接被重置）。 */
    @Value("${ip.echo-url:http://ip-api.com/line/?fields=query}")
    private String echoUrl;

    @Autowired
    private RestTemplate restTemplate;

    /** ip-api.com：国际 IP 归属地（国家/省州/城市，免费、中文），国内外均可查 */
    private static final String IP_API_URL = "http://ip-api.com/json/%s?fields=status,country,regionName,city,query&lang=zh-CN";
    /** 高德 IP 定位：国内 IP 精确到省/市 */
    private static final String AMAP_IP_URL = "https://restapi.amap.com/v3/ip?key=%s&ip=%s";

    private static final long PUBLIC_IP_TTL_MS = 30 * 60 * 1000L;

    /** ip -> [region, abnormal("0"/"1")] 缓存 */
    private final Map<String, String[]> regionCache = new ConcurrentHashMap<>();
    private volatile String cachedPublicIp;
    private volatile long publicIpExpireAt;

    /**
     * 解析「当前设备所在网络的外网 IP」。
     * 优先请求中的公网 IP；内网/回环时回退到本机公网 IP（echo，带缓存）。
     */
    public String resolvePublicIp(HttpServletRequest request) {
        String ip = ClientIpUtil.getClientIp(request);
        if (isPublic(ip)) {
            return ip;
        }
        String pub = fetchPublicIpCached();
        return pub != null ? pub : ip;
    }

    /**
     * 解析地区与异常标记。
     *
     * @return [region, abnormal]，abnormal 为 "1"(国外/异常) 或 "0"
     */
    public String[] region(String ip) {
        if (!StringUtils.hasText(ip)) {
            return new String[]{null, "0"};
        }
        if (isPrivateOrLocal(ip)) {
            return new String[]{"内网/本地", "0"};
        }
        String[] cached = regionCache.get(ip);
        if (cached != null) {
            return cached;
        }
        String[] r = lookup(ip);
        if (regionCache.size() < 5000) {
            regionCache.put(ip, r);
        }
        return r;
    }

    /**
     * 解析地区：先用 ip-api.com 取国家/城市（国内外均可）；若为中国再用高德取精确省/市。
     * 国内不异常；国外（country != 中国）显示「国家 城市」并标记异常。
     */
    private String[] lookup(String ip) {
        try {
            String body = restTemplate.getForObject(String.format(IP_API_URL, ip), String.class);
            JSONObject obj = JSON.parseObject(body);
            if (obj == null || !"success".equals(obj.getString("status"))) {
                return new String[]{"未知", "0"};
            }
            String country = obj.getString("country");
            String regionName = obj.getString("regionName");
            String city = obj.getString("city");
            if ("中国".equals(country)) {
                String amapRegion = lookupAmap(ip);
                if (StringUtils.hasText(amapRegion)) {
                    return new String[]{amapRegion, "0"};
                }
                // 高德失败：退回 ip-api 的「中国 省/州 城市」
                return new String[]{joinRegion("中国", regionName, city), "0"};
            }
            // 国外：国家 + 省/州 + 城市，标记异常
            return new String[]{joinRegion(StringUtils.hasText(country) ? country : "国外", regionName, city), "1"};
        } catch (Exception e) {
            log.warn("[IP定位] 解析失败 ip={} err={}", ip, e.getMessage());
            return new String[]{"未知", "0"};
        }
    }

    /** 拼接「国家 省/州 城市」，跳过空值与相邻重复（如省名=城市名只保留一个） */
    private String joinRegion(String... parts) {
        StringBuilder sb = new StringBuilder();
        String last = null;
        for (String p : parts) {
            if (!StringUtils.hasText(p) || p.equals(last)) {
                continue;
            }
            if (sb.length() > 0) {
                sb.append(' ');
            }
            sb.append(p);
            last = p;
        }
        return sb.toString();
    }

    /** 高德 IP 定位取国内精确省/市；失败或非国内返回 null */
    private String lookupAmap(String ip) {
        if (!StringUtils.hasText(amapKey)) {
            return null;
        }
        try {
            String body = restTemplate.getForObject(String.format(AMAP_IP_URL, amapKey, ip), String.class);
            JSONObject obj = JSON.parseObject(body);
            if (obj == null || !"1".equals(obj.getString("status"))) {
                return null;
            }
            Object province = obj.get("province");
            if (province instanceof String p && StringUtils.hasText(p)) {
                Object cityObj = obj.get("city");
                String city = cityObj instanceof String c ? c : "";
                return p + (StringUtils.hasText(city) && !city.equals(p) ? city : "");
            }
            return null;
        } catch (Exception e) {
            log.warn("[IP定位] 高德解析失败 ip={} err={}", ip, e.getMessage());
            return null;
        }
    }

    private String fetchPublicIpCached() {
        long now = System.currentTimeMillis();
        if (cachedPublicIp != null && now < publicIpExpireAt) {
            return cachedPublicIp;
        }
        try {
            String body = restTemplate.getForObject(echoUrl, String.class);
            String ip = body == null ? null : body.trim();
            if (isPublic(ip)) {
                cachedPublicIp = ip;
                publicIpExpireAt = now + PUBLIC_IP_TTL_MS;
                return ip;
            }
            log.warn("[IP定位] echo 返回非公网 IP: {}", body);
        } catch (Exception e) {
            log.warn("[IP定位] 获取本机公网 IP 失败: {}", e.getMessage());
        }
        return cachedPublicIp;
    }

    private boolean isPublic(String ip) {
        return StringUtils.hasText(ip) && !isPrivateOrLocal(ip);
    }

    private boolean isPrivateOrLocal(String ip) {
        if (!StringUtils.hasText(ip)) {
            return true;
        }
        if ("unknown".equalsIgnoreCase(ip) || "::1".equals(ip) || ip.startsWith("0:0:0:0")
                || "127.0.0.1".equals(ip) || ip.startsWith("127.")) {
            return true;
        }
        if (ip.startsWith("10.") || ip.startsWith("192.168.") || ip.startsWith("169.254.")) {
            return true;
        }
        if (ip.startsWith("172.")) {
            try {
                int second = Integer.parseInt(ip.split("\\.")[1]);
                return second >= 16 && second <= 31;
            } catch (Exception ignore) {
                return false;
            }
        }
        return false;
    }
}
