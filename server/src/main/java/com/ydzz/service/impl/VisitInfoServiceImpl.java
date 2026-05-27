package com.ydzz.service.impl;

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

import java.util.Map;

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

        // 3. 根据 IP 查找已有记录
        LambdaQueryWrapper<VisitInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(VisitInfo::getIp, query);
        VisitInfo existing = getOne(wrapper);

        // 4. 构建记录（有旧记录时先删后插，避免 UPDATE 权限问题）
        VisitInfo visitInfo = new VisitInfo();
        if (existing != null) {
            // 已有 IP — 删除旧记录，用新 ID 重新插入
            visitInfo.setId(System.currentTimeMillis() + "_" + (int) (Math.random() * 10000));
            visitInfo.setCount(existing.getCount() != null ? existing.getCount() + 1 : 1);
            baseMapper.deleteById(existing.getId());
            log.info("更新访问记录 — IP: {}, 累计访问次数: {}", query, visitInfo.getCount());
        } else {
            // 新 IP — 首次记录
            visitInfo.setId(System.currentTimeMillis() + "_" + (int) (Math.random() * 10000));
            visitInfo.setCount(1);
            log.info("创建新访问记录 — IP: {}, 国家: {}, 省份: {}, 城市: {}", query, country, province, city);
        }
        visitInfo.setIp(query);
        visitInfo.setCountry(country);
        visitInfo.setCity(city);
        visitInfo.setProvince(province);
        visitInfo.setLastVisitTime(String.valueOf(System.currentTimeMillis()));

        baseMapper.insert(visitInfo);
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
}
