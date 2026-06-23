package com.xiaomayi.system.runner;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * <p>
 * 应用初始化
 * </p>
 *
 * @author 小蚂蚁云团队
 * @since 2022-01-21
 */
@Slf4j
@Component
public class SystemApplicationRunner implements ApplicationRunner {

    /**
     * 初始化
     *
     * @param args 参数
     * @throws Exception 异常处理
     */
    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.info("应用初始化...");
    }
}
