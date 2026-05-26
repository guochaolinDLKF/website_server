package com.ydzz.server.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ydzz.server.common.Constants;
import com.ydzz.server.dto.VisitPageDTO;
import com.ydzz.server.entity.VisitPage;
import com.ydzz.server.repository.VisitPageRepository;
import com.ydzz.server.service.VisitPageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.TimeUnit;

@Log4j2
@Service
@RequiredArgsConstructor
public class VisitPageServiceImpl implements VisitPageService {

    private static final String IP_API_URL = "http://ip-api.com/json/%s?fields=status,country,city,query&lang=zh-CN";
    private static final String AMAP_URL = "https://restapi.amap.com/v3/ip?key=%s&ip=%s";
    private static final String AMAP_KEY = "a0aea882477a1b77b97eee0e8fc07a30";

    private final VisitPageRepository visitPageRepository;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final StringRedisTemplate stringRedisTemplate;

    @Override
    @Transactional
    public VisitPageDTO recordVisit(String ip) {
        String query = ip;
        String country = "";
        String city = "";
        String province = "";
        String status = "fail";

        try {
            String getIpUrl = String.format(IP_API_URL, ip);
            String result = restTemplate.getForObject(getIpUrl, String.class);
            log.info("ip-api result: {}", result);

            if (result != null) {
                JsonNode recMap = objectMapper.readTree(result);
                query = recMap.path("query").asText(ip);
                country = recMap.path("country").asText("");
                city = recMap.path("city").asText("");
                status = recMap.path("status").asText("fail");

                if ("success".equals(status) && "中国".equals(country)) {
                    String amapUrl = String.format(AMAP_URL, AMAP_KEY, query);
                    String amapResult = restTemplate.getForObject(amapUrl, String.class);
                    log.info("amap result: {}", amapResult);

                    if (amapResult != null) {
                        JsonNode chinaMap = objectMapper.readTree(amapResult);
                        city = chinaMap.path("city").asText(city);
                        province = chinaMap.path("province").asText(province);
                    }
                }
            }
        } catch (Exception e) {
            log.error("获取 IP 信息异常: {}", e.getMessage());
        }

        if (!"success".equals(status)) {
            return null;
        }

        VisitPage visitPage = visitPageRepository.findByPageKey(query).orElse(null);
        String curTime = String.valueOf(System.currentTimeMillis() / 1000);

        if (visitPage == null) {
            log.info("创建访问记录");
            visitPage = new VisitPage();
            visitPage.setId(String.valueOf(System.currentTimeMillis()));
            visitPage.setPageKey(query);
            visitPage.setCountry(country);
            visitPage.setCity(city);
            visitPage.setProvince(province);
            visitPage.setVisitCount(1);
            visitPage.setDownInfo("[]");
            visitPage.setLastVisitTime(curTime);
        } else {
            log.info("更新访问记录");
            visitPage.setCountry(country);
            visitPage.setCity(city);
            visitPage.setProvince(province);
            visitPage.setVisitCount((visitPage.getVisitCount() == null ? 0 : visitPage.getVisitCount()) + 1);
            visitPage.setLastVisitTime(curTime);
        }

        visitPage = visitPageRepository.save(visitPage);
        VisitPageDTO dto = toDTO(visitPage);

        try {
            stringRedisTemplate.opsForValue().set(Constants.REDIS_KEY_VISIT_PAGE + visitPage.getId(),
                    objectMapper.writeValueAsString(dto),
                    Constants.REDIS_CACHE_EXPIRE, TimeUnit.SECONDS);
        } catch (JsonProcessingException e) {
            log.error("缓存序列化失败: {}", e.getMessage());
        }

        return dto;
    }

    private VisitPageDTO toDTO(VisitPage entity) {
        VisitPageDTO dto = new VisitPageDTO();
        dto.setId(entity.getId());
        dto.setPageKey(entity.getPageKey());
        dto.setCountry(entity.getCountry());
        dto.setCity(entity.getCity());
        dto.setProvince(entity.getProvince());
        dto.setVisitCount(entity.getVisitCount());
        dto.setDownInfo(entity.getDownInfo());
        dto.setLastVisitTime(entity.getLastVisitTime());
        return dto;
    }
}
