package com.ydzz.server.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ydzz.server.common.Constants;
import com.ydzz.server.dto.DownInfoDTO;
import com.ydzz.server.dto.DownInfoRequestDTO;
import com.ydzz.server.dto.DownResultDTO;
import com.ydzz.server.entity.DownInfo;
import com.ydzz.server.entity.VisitPage;
import com.ydzz.server.repository.DownInfoRepository;
import com.ydzz.server.repository.VisitPageRepository;
import com.ydzz.server.service.DownInfoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Log4j2
@Service
@RequiredArgsConstructor
public class DownInfoServiceImpl implements DownInfoService {

    private final DownInfoRepository downInfoRepository;
    private final VisitPageRepository visitPageRepository;
    private final StringRedisTemplate stringRedisTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public DownInfoDTO create(DownInfoDTO dto) {
        DownInfo entity = toEntity(dto);
        entity.setCreateTime(LocalDateTime.now());
        entity.setUpdateTime(LocalDateTime.now());
        entity = downInfoRepository.save(entity);
        return toDTO(entity);
    }

    @Override
    public DownInfoDTO update(DownInfoDTO dto) {
        DownInfo entity = downInfoRepository.findById(dto.getId())
                .orElseThrow(() -> new RuntimeException("记录不存在"));
        entity.setGameName(dto.getGameName());
        entity.setVersion(dto.getVersion());
        entity.setDownloadUrl(dto.getDownloadUrl());
        entity.setDescription(dto.getDescription());
        entity.setSort(dto.getSort() != null ? dto.getSort() : 0);
        entity.setStatus(dto.getStatus() != null ? dto.getStatus() : 1);
        entity.setUpdateTime(LocalDateTime.now());
        entity = downInfoRepository.save(entity);
        deleteCache(Constants.REDIS_KEY_DOWN_INFO + dto.getId());
        return toDTO(entity);
    }

    @Override
    public void delete(Long id) {
        if (!downInfoRepository.existsById(id)) {
            throw new RuntimeException("记录不存在");
        }
        downInfoRepository.deleteById(id);
        deleteCache(Constants.REDIS_KEY_DOWN_INFO + id);
    }

    @Override
    public DownInfoDTO getById(Long id) {
        String cacheKey = Constants.REDIS_KEY_DOWN_INFO + id;
        DownInfoDTO cachedDto = getCachedDownInfo(cacheKey);
        if (cachedDto != null) {
            return cachedDto;
        }

        DownInfo entity = downInfoRepository.findById(id).orElse(null);
        if (entity == null) {
            return null;
        }

        DownInfoDTO dto = toDTO(entity);
        cacheDownInfo(cacheKey, dto);
        return dto;
    }

    @Override
    public List<DownInfoDTO> listAll() {
        return downInfoRepository.findAllByOrderBySortAsc().stream()
                .map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public boolean recordDownload(DownInfoRequestDTO request) {
        String visitIp = request.getIp();
        String downPlatform = request.getDownPlatform();

        try {
            VisitPage visitPage = visitPageRepository.findByPageKey(visitIp).orElse(null);

            if (visitPage == null) {
                log.info("VisitPage is null, current IP: {}", visitIp);
                return false;
            }

            List<DownResultDTO> downResults = parseDownInfo(visitPage.getDownInfo());

            Optional<DownResultDTO> downOpt = downResults.stream()
                    .filter(p -> downPlatform.equals(p.getPlatform()))
                    .findFirst();

            String curTime = String.valueOf(System.currentTimeMillis() / 1000);

            if (downOpt.isPresent()) {
                DownResultDTO downResult = downOpt.get();
                downResult.setDownCount(downResult.getDownCount() + 1);
                downResult.setDownTime(curTime);
            } else {
                DownResultDTO downResult = new DownResultDTO();
                downResult.setPlatform(downPlatform);
                downResult.setDownCount(1);
                downResult.setDownTime(curTime);
                downResults.add(downResult);
            }

            String saveData = objectMapper.writeValueAsString(downResults);
            log.info("更新后的下载信息: {}", saveData);
            visitPage.setDownInfo(saveData);
            visitPageRepository.save(visitPage);

            deleteCache(Constants.REDIS_KEY_VISIT_PAGE + visitPage.getId());
            return true;
        } catch (Exception e) {
            log.error("记录下载失败: {}", e.getMessage(), e);
            return false;
        }
    }

    private List<DownResultDTO> parseDownInfo(String downInfoJson) {
        if (downInfoJson == null || downInfoJson.isBlank() || "[]".equals(downInfoJson)) {
            return new ArrayList<>();
        }
        try {
            return objectMapper.readValue(downInfoJson, new TypeReference<List<DownResultDTO>>() {});
        } catch (JsonProcessingException e) {
            log.error("解析下载信息异常: {}", e.getMessage());
            return new ArrayList<>();
        }
    }

    private DownInfoDTO getCachedDownInfo(String cacheKey) {
        try {
            String cached = stringRedisTemplate.opsForValue().get(cacheKey);
            if (cached == null) {
                return null;
            }
            return objectMapper.readValue(cached, DownInfoDTO.class);
        } catch (Exception e) {
            log.warn("读取下载信息缓存失败: {}", e.getMessage());
            return null;
        }
    }

    private void cacheDownInfo(String cacheKey, DownInfoDTO dto) {
        try {
            stringRedisTemplate.opsForValue().set(cacheKey, objectMapper.writeValueAsString(dto),
                    Constants.REDIS_CACHE_EXPIRE, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.warn("写入下载信息缓存失败: {}", e.getMessage());
        }
    }

    private void deleteCache(String cacheKey) {
        try {
            stringRedisTemplate.delete(cacheKey);
        } catch (Exception e) {
            log.warn("删除缓存失败: {}", e.getMessage());
        }
    }

    private DownInfo toEntity(DownInfoDTO dto) {
        DownInfo entity = new DownInfo();
        entity.setId(dto.getId());
        entity.setGameName(dto.getGameName());
        entity.setVersion(dto.getVersion());
        entity.setDownloadUrl(dto.getDownloadUrl());
        entity.setDescription(dto.getDescription());
        entity.setSort(dto.getSort() != null ? dto.getSort() : 0);
        entity.setStatus(dto.getStatus() != null ? dto.getStatus() : 1);
        return entity;
    }

    private DownInfoDTO toDTO(DownInfo entity) {
        DownInfoDTO dto = new DownInfoDTO();
        dto.setId(entity.getId());
        dto.setGameName(entity.getGameName());
        dto.setVersion(entity.getVersion());
        dto.setDownloadUrl(entity.getDownloadUrl());
        dto.setDescription(entity.getDescription());
        dto.setSort(entity.getSort());
        dto.setStatus(entity.getStatus());
        return dto;
    }
}
