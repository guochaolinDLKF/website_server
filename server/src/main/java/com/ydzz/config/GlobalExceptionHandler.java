package com.ydzz.config;

import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.exception.NotPermissionException;
import cn.dev33.satoken.exception.NotRoleException;
import com.ydzz.common.ErrorCode;
import com.ydzz.common.Result;
import com.ydzz.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import java.util.stream.Collectors;

/**
 * 全局异常处理器
 * 统一处理各种异常，返回标准化的错误响应
 * 
 * @author FortuneTelling
 * @since 1.0.0
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理 Sa-Token 未登录异常
     */
    @ExceptionHandler(NotLoginException.class)
    public ResponseEntity<Result<?>> handleNotLogin(NotLoginException e) {
        String msg = switch (e.getType()) {
            case NotLoginException.NOT_TOKEN -> "未提供 Token";
            case NotLoginException.INVALID_TOKEN -> "Token 无效";
            case NotLoginException.TOKEN_TIMEOUT -> "Token 已过期";
            case NotLoginException.BE_REPLACED -> "Token 已被顶下线";
            case NotLoginException.KICK_OUT -> "Token 已被踢下线";
            default -> "未登录";
        };
        
        log.warn("用户未登录: {}", msg);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Result.of(ErrorCode.Unauthorized, msg));
    }

    /**
     * 处理 Sa-Token 权限不足异常
     */
    @ExceptionHandler({ NotPermissionException.class, NotRoleException.class })
    public ResponseEntity<Result<?>> handleForbidden(RuntimeException e) {
        String msg = e instanceof NotPermissionException ? "权限不足" : "角色不符";
        
        log.warn("权限不足: {}", msg);
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(Result.of(ErrorCode.Forbidden, msg));
    }

    /**
     * 处理参数校验异常
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Result<?>> handleValidationException(MethodArgumentNotValidException e) {
        String msg = e.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));
        
        log.warn("参数校验失败: {}", msg);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Result.of(ErrorCode.BadRequest, msg));
    }

    /**
     * 处理绑定异常
     */
    @ExceptionHandler(BindException.class)
    public ResponseEntity<Result<?>> handleBindException(BindException e) {
        String msg = e.getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));
        
        log.warn("参数绑定失败: {}", msg);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Result.of(ErrorCode.BadRequest, msg));
    }

    /**
     * 处理约束违反异常
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Result<?>> handleConstraintViolationException(ConstraintViolationException e) {
        String msg = e.getConstraintViolations().stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.joining(", "));
        
        log.warn("约束校验失败: {}", msg);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Result.of(ErrorCode.BadRequest, msg));
    }

    /**
     * 处理缺少请求参数异常
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<Result<?>> handleMissingParameterException(MissingServletRequestParameterException e) {
        String msg = "缺少必需参数: " + e.getParameterName();
        
        log.warn("缺少请求参数: {}", msg);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Result.of(ErrorCode.BadRequest, msg));
    }

    /**
     * 处理参数类型不匹配异常
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Result<?>> handleTypeMismatchException(MethodArgumentTypeMismatchException e) {
        String msg = "参数类型错误: " + e.getName() + " 应为 " + e.getRequiredType().getSimpleName();
        
        log.warn("参数类型不匹配: {}", msg);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Result.of(ErrorCode.BadRequest, msg));
    }

    /**
     * 处理资源不存在异常
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<Result<?>> handleNoHandlerFoundException(NoHandlerFoundException e) {
        String msg = "请求的资源不存在: " + e.getRequestURL();
        
        log.warn("资源不存在: {}", msg);
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Result.of(ErrorCode.NotFound, msg));
    }

    /**
     * 处理业务异常
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Result<?>> handleIllegalArgumentException(IllegalArgumentException e) {
        log.warn("业务参数错误: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Result.of(ErrorCode.BadRequest, e.getMessage()));
    }

    /**
     * 处理业务异常
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<Result<?>> handleBusinessException(BusinessException e) {
        log.warn("业务异常: [{}] {}", e.getErrorCode().getCode(), e.getMessage());
        log.info("错误码详情: code={}, message={}, class={}", 
                e.getErrorCode().getCode(), e.getErrorCode().getMessage(), e.getErrorCode().getClass().getName());
        
        Result<?> result = Result.of(e.getErrorCode(), e.getMessage());
        log.info("返回结果: code={}, message={}, httpStatus={}, resultClass={}", 
                result.getCode(), result.getMessage(), HttpStatus.BAD_REQUEST.value(), result.getClass().getName());
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(result);
    }

    /**
     * 处理业务状态异常
     */
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Result<?>> handleIllegalStateException(IllegalStateException e) {
        log.warn("业务状态错误: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Result.of(ErrorCode.BadRequest, e.getMessage()));
    }

    /**
     * 处理运行时异常
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Result<?>> handleRuntimeException(RuntimeException e) {
        log.error("运行时异常: ", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Result.of(ErrorCode.InternalServerError, "系统运行异常"));
    }

    /**
     * 处理所有其他异常
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Result<?>> handleException(Exception e) {
        log.error("系统异常: ", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Result.of(ErrorCode.InternalServerError, "系统内部错误"));
    }
} 