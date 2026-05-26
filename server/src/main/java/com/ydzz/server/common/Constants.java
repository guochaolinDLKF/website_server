package com.ydzz.server.common;

public class Constants {
    public static final String REDIS_KEY_PREFIX = "bazi:";
    public static final String REDIS_KEY_DOWN_INFO = REDIS_KEY_PREFIX + "down_info:";
    public static final String REDIS_KEY_VISIT_PAGE = REDIS_KEY_PREFIX + "visit_page:";
    public static final long REDIS_CACHE_EXPIRE = 30 * 60;
}