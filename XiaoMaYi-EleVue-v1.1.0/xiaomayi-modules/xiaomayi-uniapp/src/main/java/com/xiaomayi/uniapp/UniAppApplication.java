package com.xiaomayi.uniapp;

import com.alibaba.druid.spring.boot3.autoconfigure.DruidDataSourceAutoConfigure;
import com.baomidou.mybatisplus.autoconfigure.MybatisPlusAutoConfiguration;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * 启动类排除Druid数据源自动配置类
 */
@SpringBootApplication(exclude = {
        DataSourceAutoConfiguration.class,
        DruidDataSourceAutoConfigure.class,
        MybatisPlusAutoConfiguration.class,
        SecurityAutoConfiguration.class
}, scanBasePackages = {"com.xiaomayi.*"})
//@MapperScan("com.xiaomayi.**.mapper")
// 开启全局事务
@EnableTransactionManagement
public class UniAppApplication {
    public static void main(String[] args) {
        SpringApplication.run(UniAppApplication.class, args);
        System.out.println("(♥◠‿◠)ﾉﾞ  小蚂蚁云客户端服务启动成功   ლ(´ڡ`ლ)ﾞ");
    }
}