package com.ydzz.enums;

import lombok.Getter;

/**
 * 用户状态枚举
 * 
 * @author FortuneTelling
 * @since 1.0.0
 */
@Getter
public enum UserStatus {
    
    DISABLED(0, "禁用"),
    ENABLED(1, "启用");
    
    private final Integer code;
    private final String description;
    
    UserStatus(Integer code, String description) {
        this.code = code;
        this.description = description;
    }
    
    /**
     * 根据代码获取枚举
     */
    public static UserStatus fromCode(Integer code) {
        for (UserStatus status : values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        return DISABLED;
    }
} 