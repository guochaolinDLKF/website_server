package com.ydzz;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

import javax.sql.DataSource;
import java.sql.Connection;

/**
 * 应用程序主入口
 *
 * @author WebsiteServer
 * @since 1.0.0
 */
@Slf4j
@SpringBootApplication
@MapperScan({"com.ydzz.mapper", "com.ydzz.admin.mapper", "com.ydzz.admin.business.mapper"})
@EnableAsync
@EnableScheduling
public class WebsiteServerApplication {

    @Autowired
    private DataSource dataSource;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(WebsiteServerApplication.class, args);
        ConfigurableEnvironment env = context.getEnvironment();
        String port = env.getProperty("server.port", "8080");
        String address = env.getProperty("server.address");
        String host = (address != null && !address.isEmpty()) ? address : "localhost";
        String contextPath = env.getProperty("server.servlet.context-path", "");

        log.info("========================================");
        log.info("  应用启动成功！");
        log.info("  访问地址: http://{}:{}{}", host, port, contextPath);
        log.info("  Swagger:  http://{}:{}{}/swagger-ui.html", host, port, contextPath);
        log.info("========================================");
    }

    /**
     * 应用启动完成后立即执行一次健康检查
     */
    @EventListener(ApplicationReadyEvent.class)
    public void onStartupHealthCheck() {
        log.info("========== 启动健康检查 ==========");
        checkDatabase();
        checkRedis();
        log.info("========== 启动健康检查完成 ==========");
    }

    private void checkDatabase() {
        try (Connection conn = dataSource.getConnection()) {
            log.info("[健康检查] MySQL 连接正常 — URL: {}, 产品: {} {}",
                    conn.getMetaData().getURL(),
                    conn.getMetaData().getDatabaseProductName(),
                    conn.getMetaData().getDatabaseProductVersion());
        } catch (Exception e) {
            log.error("[健康检查] MySQL 连接失败 — {}", e.getMessage());
        }
    }

    private void checkRedis() {
        try {
            String pong = stringRedisTemplate.getConnectionFactory()
                    .getConnection().ping();
            log.info("[健康检查] Redis 连接正常 — ping 响应: {}", pong);
        } catch (Exception e) {
            log.error("[健康检查] Redis 连接失败 — {}", e.getMessage());
        }
    }
}
