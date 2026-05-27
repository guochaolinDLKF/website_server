package com.ydzz.service.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ydzz.entity.VisitInfo;
import com.ydzz.mapper.VisitInfoMapper;
import com.ydzz.service.VisitInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * 访问信息服务实现
 * 记录每次网页访问的 IP 信息、归属地、访问次数
 *
 * @author WebsiteServer
 * @since 1.0.0
 */
@Slf4j
@Service
public class VisitInfoServiceImpl extends ServiceImpl<VisitInfoMapper, VisitInfo> implements VisitInfoService {

    @Autowired
    private RestTemplate restTemplate;

    /** ip-api.com 查询地址（国际 IP 归属地查询，免费，无需 API Key） */
    private static final String IP_API_URL = "http://ip-api.com/json/%s?fields=status,country,city,query&lang=zh-CN";

    /** 高德地图 IP 定位 API（国内 IP 精确到省份城市，需申请 Key） */
    private static final String AMAP_IP_URL = "https://restapi.amap.com/v3/ip?key=a0aea882477a1b77b97eee0e8fc07a30&ip=%s";

    @Override
    public void recordVisit(String ip) {
        // 本地回环地址跳过，不保存到数据库
        if (isLocalhost(ip)) {
            log.debug("检测到本机 IP: {}，跳过记录", ip);
            return;
        }
        // 1. 调用 ip-api.com 查询 IP 基本信息
        String ipApiUrl = String.format(IP_API_URL, ip);
        String result = restTemplate.getForObject(ipApiUrl, String.class);
        log.info("ip-api 返回: {}", result);

        IpApiResult ipResult = null;
        try {
            ipResult = JSONObject.parseObject(result, IpApiResult.class);
        } catch (Exception e) {
            log.error("解析 ip-api 返回数据失败: {}", e.getMessage());
            return;
        }

        if (ipResult == null || !"success".equals(ipResult.status)) {
            log.info("查询 IP: {} 失败, status: {}", ip, ipResult != null ? ipResult.status : "null");
            return;
        }

        String query = ipResult.query;
        String country = ipResult.country;
        String city = ipResult.city;
        String province = "";

        log.info("IP 查询结果 — IP: {}, 国家: {}, 城市: {}", query, country, city);

        // 2. 如果是国内 IP，通过高德地图获取更精确的省份和城市
        if ("中国".equals(country)) {
            try {
                String amapUrl = String.format(AMAP_IP_URL, query);
                String amapResult = restTemplate.getForObject(amapUrl, String.class);
                log.info("高德地图返回: {}", amapResult);

                @SuppressWarnings("unchecked")
                Map<String, String> chinaMap = JSONObject.parseObject(amapResult, Map.class);
                city = chinaMap.get("city");
                province = chinaMap.get("province");

                log.info("高德地图解析 — 省份: {}, 城市: {}", province, city);
            } catch (Exception e) {
                log.error("调用高德地图 API 失败: {}", e.getMessage());
                // 高德失败不影响主流程，继续使用 ip-api 的结果
            }
        }

        // 3. 保存或更新访问记录
        saveOrUpdateVisit(query, country, city, province);
    }

    /**
     * 保存或更新访问记录
     */
    private void saveOrUpdateVisit(String ip, String country, String city, String province) {
        LambdaQueryWrapper<VisitInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(VisitInfo::getIp, ip);
        VisitInfo visitInfo = getOne(wrapper);

        if (visitInfo != null) {
            visitInfo.setCount(visitInfo.getCount() != null ? visitInfo.getCount() + 1 : 1);
            visitInfo.setLastVisitTime(String.valueOf(System.currentTimeMillis()));
            visitInfo.setCountry(country);
            visitInfo.setCity(city);
            if (province != null && !province.isEmpty()) {
                visitInfo.setProvince(province);
            }
            updateById(visitInfo);
            log.info("更新访问记录 — IP: {}, 累计访问次数: {}", ip, visitInfo.getCount());
        } else {
            visitInfo = new VisitInfo();
            visitInfo.setId(System.currentTimeMillis() + "_" + (int) (Math.random() * 10000));
            visitInfo.setIp(ip);
            visitInfo.setCountry(country);
            visitInfo.setCity(city);
            visitInfo.setProvince(province);
            visitInfo.setCount(1);
            visitInfo.setLastVisitTime(String.valueOf(System.currentTimeMillis()));
            save(visitInfo);
            log.info("创建新访问记录 — IP: {}, 国家: {}, 省份: {}, 城市: {}", ip, country, province, city);
        }
    }

    /**
     * 判断是否为本地回环地址，这类 IP 无需查询外部归属地接口
     */
    private boolean isLocalhost(String ip) {
        return "127.0.0.1".equals(ip)
                || "0:0:0:0:0:0:0:1".equals(ip)
                || "::1".equals(ip)
                || "localhost".equalsIgnoreCase(ip);
    }

    @Override
    public void recordDownload(String ip, String platform) {
        // 1. 根据 IP 查找已有记录
        LambdaQueryWrapper<VisitInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(VisitInfo::getIp, ip);
        VisitInfo existing = getOne(wrapper);

        if (existing == null) {
            log.warn("下载记录失败 — IP: {} 尚未有访问记录（需先调用 visit_page 接口）", ip);
            return;
        }

        // 2. 解析已有下载信息 JSON 数组
        String now = String.valueOf(System.currentTimeMillis());
        List<DownResult> downResults;
        String downInfoJson = existing.getDownInfo();
        if (downInfoJson != null && !downInfoJson.isEmpty()) {
            try {
                downResults = JSON.parseArray(downInfoJson, DownResult.class);
            } catch (Exception e) {
                log.warn("解析下载信息 JSON 失败，将重新初始化 — IP: {}, 原始数据: {}", ip, downInfoJson);
                downResults = new ArrayList<>();
            }
        } else {
            downResults = new ArrayList<>();
        }

        // 3. 查找是否已有同平台记录，有则累加，无则新增
        Optional<DownResult> existingDown = downResults.stream()
                .filter(d -> platform.equals(d.platform))
                .findFirst();

        if (existingDown.isPresent()) {
            DownResult dr = existingDown.get();
            dr.downCount++;
            dr.downTime = now;
            log.info("更新下载记录 — IP: {}, 平台: {}, 累计下载次数: {}", ip, platform, dr.downCount);
        } else {
            DownResult dr = new DownResult();
            dr.platform = platform;
            dr.downCount = 1;
            dr.downTime = now;
            downResults.add(dr);
            log.info("新增下载记录 — IP: {}, 平台: {}, 下载次数: 1", ip, platform);
        }

        // 4. 序列化并更新数据库
        String saveData = JSON.toJSONString(downResults);
        log.info("更新的下载信息: {}", saveData);

        existing.setDownInfo(saveData);
        updateById(existing);
    }

    /**
     * ip-api.com 返回结果映射
     */
    @SuppressWarnings("unused")
    public static class IpApiResult {
        public String status;
        public String country;
        public String city;
        public String query;
    }

    /**
     * 下载信息条目（存储在 visitinfo.downInfo 字段中，JSON 数组格式）
     */
    @SuppressWarnings("unused")
    public static class DownResult {
        /** 下载平台标识 */
        public String platform;
        /** 该平台累计下载次数 */
        public int downCount;
        /** 最近一次下载时间（毫秒时间戳） */
        public String downTime;

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            DownResult that = (DownResult) obj;
            return Objects.equals(platform, that.platform);
        }

        @Override
        public int hashCode() {
            return Objects.hash(platform);
        }
    }
}
