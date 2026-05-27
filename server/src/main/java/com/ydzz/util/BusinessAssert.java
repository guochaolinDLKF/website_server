package com.ydzz.util;

import com.ydzz.common.ErrorCode;
import com.ydzz.exception.BusinessException;
import cn.hutool.core.util.StrUtil;

/**
 * 业务断言工具类
 * 用于快速抛出业务异常
 * 
 * @author FortuneTelling
 * @since 1.0.0
 */
public class BusinessAssert {
    
    /**
     * 断言为真，否则抛出业务异常
     * 
     * @param condition 条件
     * @param errorCode 错误码
     */
    public static void isTrue(boolean condition, ErrorCode errorCode) {
        if (!condition) {
            throw BusinessException.of(errorCode);
        }
    }
    
    /**
     * 断言为真，否则抛出业务异常
     * 
     * @param condition 条件
     * @param errorCode 错误码
     * @param message 错误信息
     */
    public static void isTrue(boolean condition, ErrorCode errorCode, String message) {
        if (!condition) {
            throw BusinessException.of(errorCode, message);
        }
    }
    
    /**
     * 断言为假，否则抛出业务异常
     * 
     * @param condition 条件
     * @param errorCode 错误码
     */
    public static void isFalse(boolean condition, ErrorCode errorCode) {
        if (condition) {
            throw BusinessException.of(errorCode);
        }
    }
    
    /**
     * 断言为假，否则抛出业务异常
     * 
     * @param condition 条件
     * @param errorCode 错误码
     * @param message 错误信息
     */
    public static void isFalse(boolean condition, ErrorCode errorCode, String message) {
        if (condition) {
            throw BusinessException.of(errorCode, message);
        }
    }
    
    /**
     * 断言非空，否则抛出业务异常
     * 
     * @param object 对象
     * @param errorCode 错误码
     */
    public static void notNull(Object object, ErrorCode errorCode) {
        if (object == null) {
            throw BusinessException.of(errorCode);
        }
    }
    
    /**
     * 断言非空，否则抛出业务异常
     * 
     * @param object 对象
     * @param errorCode 错误码
     * @param message 错误信息
     */
    public static void notNull(Object object, ErrorCode errorCode, String message) {
        if (object == null) {
            throw BusinessException.of(errorCode, message);
        }
    }
    
    /**
     * 断言非空字符串，否则抛出业务异常
     * 
     * @param str 字符串
     * @param errorCode 错误码
     */
    public static void notBlank(String str, ErrorCode errorCode) {
        if (StrUtil.isBlank(str)) {
            throw BusinessException.of(errorCode);
        }
    }
    
    /**
     * 断言非空字符串，否则抛出业务异常
     * 
     * @param str 字符串
     * @param errorCode 错误码
     * @param message 错误信息
     */
    public static void notBlank(String str, ErrorCode errorCode, String message) {
        if (StrUtil.isBlank(str)) {
            throw BusinessException.of(errorCode, message);
        }
    }
    
    /**
     * 断言相等，否则抛出业务异常
     * 
     * @param obj1 对象1
     * @param obj2 对象2
     * @param errorCode 错误码
     */
    public static void equals(Object obj1, Object obj2, ErrorCode errorCode) {
        if (!java.util.Objects.equals(obj1, obj2)) {
            throw BusinessException.of(errorCode);
        }
    }
    
    /**
     * 断言相等，否则抛出业务异常
     * 
     * @param obj1 对象1
     * @param obj2 对象2
     * @param errorCode 错误码
     * @param message 错误信息
     */
    public static void equals(Object obj1, Object obj2, ErrorCode errorCode, String message) {
        if (!java.util.Objects.equals(obj1, obj2)) {
            throw BusinessException.of(errorCode, message);
        }
    }
} 