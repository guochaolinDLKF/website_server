package com.ydzz.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * MyBatis Plus配置类
 * 
 * @author FortuneTelling
 * @since 1.0.0
 */
@Configuration
public class MybatisPlusConfig {

    /**
     * 分页插件
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        return interceptor;
    }

    /**
     * 自动填充处理器
     */
    @Component
    public static class MyMetaObjectHandler implements MetaObjectHandler {

        @Override
        public void insertFill(MetaObject metaObject) {
            // 自动填充创建时间
            this.strictInsertFill(metaObject, "createTime", LocalDateTime.class, LocalDateTime.now());
            // 自动填充更新时间
            this.strictInsertFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now());
            // 自动填充删除标识
            this.strictInsertFill(metaObject, "deletedFlag", String.class, "N");
            // 自动填充创建人（从当前用户上下文获取）
            String currentUserId = getCurrentUserId();
            if (currentUserId != null) {
                this.strictInsertFill(metaObject, "creator", String.class, currentUserId);
                this.strictInsertFill(metaObject, "updater", String.class, currentUserId);
            }
        }

        @Override
        public void updateFill(MetaObject metaObject) {
            // 自动填充更新时间
            this.strictUpdateFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now());
            // 自动填充更新人（从当前用户上下文获取）
            String currentUserId = getCurrentUserId();
            if (currentUserId != null) {
                this.strictUpdateFill(metaObject, "updater", String.class, currentUserId);
            }
        }

        /**
         * 获取当前用户ID
         * 从Sa-Token上下文中获取当前登录用户ID
         */
        private String getCurrentUserId() {
            try {
                // 尝试从Sa-Token获取当前用户ID
                Object loginId = cn.dev33.satoken.stp.StpUtil.getLoginIdDefaultNull();
                if (loginId != null) {
                    return loginId.toString();
                }
            } catch (Exception e) {
                // 如果获取失败，返回null，不填充用户信息
                // 这样可以支持非登录状态下的数据操作
            }
            return null;
        }
    }
} 