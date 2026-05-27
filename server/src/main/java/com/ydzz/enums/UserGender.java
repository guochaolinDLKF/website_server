package com.ydzz.enums;

import lombok.Getter;

/**
 * 用户性别枚举
 * 
 * @author FortuneTelling
 * @since 1.0.0
 */
@Getter
public enum UserGender {
    
    FEMALE(0, "女"),
    MALE(1, "男");
    
    private final Integer code;
    private final String description;
    
    UserGender(Integer code, String description) {
        this.code = code;
        this.description = description;
    }
    
    /**
     * 根据代码获取枚举
     */
    public static UserGender fromCode(Integer code) {
        for (UserGender gender : values()) {
            if (gender.getCode().equals(code)) {
                return gender;
            }
        }
        return MALE; // 默认返回男性
    }
} 