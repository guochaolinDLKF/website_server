package com.ydzz.exception;

import com.ydzz.common.ErrorCode;
import lombok.Getter;

/**
 * 业务异常类
 * 用于处理业务逻辑异常，包含错误码和错误信息
 * 
 * @author FortuneTelling
 * @since 1.0.0
 */
@Getter
public class BusinessException extends RuntimeException {
    
    private final ErrorCode errorCode;
    
    /**
     * 构造函数
     * 
     * @param errorCode 错误码
     */
    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
    
    /**
     * 构造函数
     * 
     * @param message 错误信息
     */
    public BusinessException(String message) {
        super(message);
        this.errorCode = ErrorCode.ServerError;
    }
    
    /**
     * 构造函数
     * 
     * @param errorCode 错误码
     * @param message 错误信息
     */
    public BusinessException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }
    
    /**
     * 构造函数
     * 
     * @param errorCode 错误码
     * @param message 错误信息
     * @param cause 原因
     */
    public BusinessException(ErrorCode errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }
    
    /**
     * 构造函数
     * 
     * @param errorCode 错误码
     * @param cause 原因
     */
    public BusinessException(ErrorCode errorCode, Throwable cause) {
        super(errorCode.getMessage(), cause);
        this.errorCode = errorCode;
    }
    
    /**
     * 快速创建业务异常
     * 
     * @param errorCode 错误码
     * @return 业务异常
     */
    public static BusinessException of(ErrorCode errorCode) {
        return new BusinessException(errorCode);
    }
    
    /**
     * 快速创建业务异常
     * 
     * @param errorCode 错误码
     * @param message 错误信息
     * @return 业务异常
     */
    public static BusinessException of(ErrorCode errorCode, String message) {
        return new BusinessException(errorCode, message);
    }
} 