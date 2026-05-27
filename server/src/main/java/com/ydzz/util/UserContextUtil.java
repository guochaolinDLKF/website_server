package com.ydzz.util;

import cn.dev33.satoken.stp.StpUtil;
import com.ydzz.entity.User;
import com.ydzz.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 用户上下文工具类
 * 提供获取当前登录用户信息的便捷方法
 */
@Slf4j
@Component
public class UserContextUtil {
    
    private static UserService userService;
    
    @Autowired
    public void setUserService(UserService userService) {

        UserContextUtil.userService = userService;
    }
    
    /**
     * 获取当前登录用户ID
     */
    public static Long getCurrentUserId() {
        try {
            return StpUtil.getLoginIdAsLong();
        } catch (Exception e) {
            log.warn("获取当前用户ID失败: {}", e.getMessage());
            return null;
        }
    }
    
    /**
     * 获取当前登录用户信息（优先从缓存获取）
     */
    public static User getCurrentUser() {
        Long userId = getCurrentUserId();
        if (userId == null) {
            return null;
        }
        
        try {
            return userService.getById(userId);
        } catch (Exception e) {
            log.error("获取当前用户信息失败，用户ID: {}", userId, e);
            return null;
        }
    }
    
    /**
     * 检查当前用户是否已登录
     */
    public static boolean isLoggedIn() {
        try {
            return StpUtil.isLogin();
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * 获取当前用户手机号
     */
    public static String getCurrentUserPhone() {
        User user = getCurrentUser();
        return user != null ? user.getPhoneCode() : null;
    }
    
    /**
     * 获取当前用户昵称
     */
    public static String getCurrentUserNickname() {
        User user = getCurrentUser();
        return user != null ? user.getNickName() : null;
    }
}
