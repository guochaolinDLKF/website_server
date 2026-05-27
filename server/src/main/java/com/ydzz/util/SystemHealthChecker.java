package com.ydzz.util;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;

/**
 * 系统健康检查组件
 * 定时检查数据库和 Redis 连接状态并记录日志
 *
 * @author FortuneTelling
 * @since 1.0.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SystemHealthChecker {

    private final DataSource dataSource;
    private final StringRedisTemplate stringRedisTemplate;

    /**
     * 健康检查间隔（毫秒），默认 5 分钟
     */
    @Value("${health.check.interval-ms:300000}")
    private long checkIntervalMs;

    /**
     * 定时健康检查
     * 每隔固定时间检查一次数据库和 Redis 的连接状态
     */
    @Scheduled(fixedRateString = "${health.check.interval-ms:300000}")
    public void scheduledHealthCheck() {
        log.debug("========== 定时健康检查 ==========");
        checkDatabase();
        checkRedis();
    }

    private void checkDatabase() {
        try (Connection conn = dataSource.getConnection()) {
            log.debug("[定时检查] MySQL 连接正常");
        } catch (Exception e) {
            log.error("[定时检查] MySQL 连接异常 — {}", e.getMessage());
        }
    }

    private void checkRedis() {
        try {
            stringRedisTemplate.getConnectionFactory().getConnection().ping();
            log.debug("[定时检查] Redis 连接正常");
        } catch (Exception e) {
            log.error("[定时检查] Redis 连接异常 — {}", e.getMessage());
        }
    }
}
