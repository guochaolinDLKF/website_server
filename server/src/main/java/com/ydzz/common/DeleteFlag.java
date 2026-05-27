package com.ydzz.common;

/**
 * 删除标记枚举类
 * N: 未删除
 * Y: 已删除
 */
public enum DeleteFlag {
    /**
     * 未删除
     */
    NOT_DELETED("N"),

    /**
     * 已删除
     */
    DELETED("Y");

    private final String value;

    DeleteFlag(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
