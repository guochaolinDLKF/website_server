package com.ydzz.admin.config;

import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpLogic;

/**
 * 后台管理端独立登录域工具。
 *
 * <p>使用独立的 Sa-Token 账号体系（loginType = "admin"），与 C 端用户态（默认 "login"）
 * 完全隔离，互不挤下线。后台接口的鉴权、权限注解均基于该登录域。</p>
 *
 * @author WebsiteServer
 * @since 1.0.0
 */
public final class AdminStpUtil {

    /** 后台登录域类型，对应 {@code @SaCheckPermission(type = "admin")} */
    public static final String TYPE = "admin";

    /** 独立 StpLogic，构造时会自动注册到 SaManager */
    public static final StpLogic STP_LOGIC = new StpLogic(TYPE);

    private AdminStpUtil() {
    }

    public static void login(Object adminId) {
        STP_LOGIC.login(adminId);
    }

    public static String getTokenValue() {
        return STP_LOGIC.getTokenValue();
    }

    public static SaTokenInfo getTokenInfo() {
        return STP_LOGIC.getTokenInfo();
    }

    public static long getLoginIdAsLong() {
        return STP_LOGIC.getLoginIdAsLong();
    }

    public static Object getLoginIdDefaultNull() {
        return STP_LOGIC.getLoginIdDefaultNull();
    }

    public static boolean isLogin() {
        return STP_LOGIC.isLogin();
    }

    public static void checkLogin() {
        STP_LOGIC.checkLogin();
    }

    public static void logout() {
        STP_LOGIC.logout();
    }
}
