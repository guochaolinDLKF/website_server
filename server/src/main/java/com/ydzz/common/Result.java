package com.ydzz.common;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 通用响应结果类
 * 
 * @author FortuneTelling
 * @since 1.0.0
 */
@Data
@Schema(description = "统一响应结果")
public class Result<T> {

    /**
     * 响应码
     */
    @Schema(description = "响应码", example = "0")
    private Integer code;

    /**
     * 响应消息
     */
    @Schema(description = "响应消息", example = "操作成功")
    private String message;

    /**
     * 响应数据
     */
    @Schema(description = "响应数据")
    private T data;

    /**
     * 时间戳
     */
    @Schema(description = "时间戳", example = "1704067200000")
    private Long timestamp;

    public Result() {
        this.timestamp = System.currentTimeMillis();
    }

    public Result(Integer code, String message) {
        this();
        this.code = code;
        this.message = message;
    }

    public Result(Integer code, String message, T data) {
        this(code, message);
        this.data = data;
    }

    /**
     * 成功响应
     */
    public static <T> Result<T> success() {
        return new Result<>(0, "操作成功");
    }

    /**
     * 成功响应（带数据）
     */
    public static <T> Result<T> success(T data) {
        return new Result<>(0, "操作成功", data);
    }

    /**
     * 成功响应（自定义消息）
     */
    public static <T> Result<T> success(String message, T data) {
        return new Result<>(0, message, data);
    }

    /**
     * 失败响应
     */
    public static <T> Result<T> error() {
        return new Result<>(ErrorCode.ServerError.getCode(), ErrorCode.ServerError.getMessage());
    }

    /**
     * 失败响应（自定义消息）
     */
    public static <T> Result<T> error(String message) {
        return new Result<>(ErrorCode.ServerError.getCode(), message);
    }

    /**
     * 失败响应（自定义码和消息）
     */
    public static <T> Result<T> error(Integer code, String message) {
        return new Result<>(code, message);
    }

    /**
     * 根据 ErrorCode 构建响应
     */
    public static <T> Result<T> of(ErrorCode errorCode) {
        return new Result<>(errorCode.getCode(), errorCode.getMessage());
    }

    /**
     * 根据 ErrorCode 构建响应（带数据）
     */
    public static <T> Result<T> of(ErrorCode errorCode, T data) {
        return new Result<>(errorCode.getCode(), errorCode.getMessage(), data);
    }

    /**
     * 未授权响应
     */
    public static <T> Result<T> unauthorized() {
        return new Result<>(401, "未授权访问");
    }

    /**
     * 禁止访问响应
     */
    public static <T> Result<T> forbidden() {
        return new Result<>(403, "禁止访问");
    }

    /**
     * 资源不存在响应
     */
    public static <T> Result<T> notFound() {

        return new Result<>(404, "资源不存在");
    }
} 