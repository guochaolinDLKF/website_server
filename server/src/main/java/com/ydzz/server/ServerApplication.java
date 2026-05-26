package com.ydzz.server;

import lombok.extern.log4j.Log4j2;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;
import org.springframework.util.StringUtils;

@Log4j2
@SpringBootApplication
public class ServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServerApplication.class, args);
    }

    @EventListener(ApplicationReadyEvent.class)
    public void logStartupInfo(ApplicationReadyEvent event) {
        Environment environment = event.getApplicationContext().getEnvironment();
        String port = environment.getProperty("local.server.port",
                environment.getProperty("server.port", "8080"));
        String contextPath = normalizePath(environment.getProperty("server.servlet.context-path", ""));
        String swaggerPath = normalizePath(environment.getProperty("springdoc.swagger-ui.path", "/swagger-ui/index.html"));
        String swaggerUrl = "http://localhost:" + port + contextPath + swaggerPath;

        log.info("服务器启动成功，端口: {}", port);
        log.info("Swagger 访问地址: {}", swaggerUrl);
    }

    private String normalizePath(String path) {
        if (!StringUtils.hasText(path) || "/".equals(path)) {
            return "";
        }
        return path.startsWith("/") ? path : "/" + path;
    }
}
