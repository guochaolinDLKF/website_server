package com.ydzz.common;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "错误码定义")
public enum ErrorCode {
    Success(0, "成功"),
    PhoneIsNull(-1, "手机号不能为空"),
    PhoneIsError(-2, "手机号格式错误"),
    UserIdIsNull(-3, "用户Id不能为空"),
    ServerError(-4, "服务器异常"),
    UserIsNull(-5, "找不到这样的用户"),
    VerfityCodeIsNull(-6, "验证码不能为空"),
    FieldError(-7, "字段异常，请检查参数"),
    AccountIsNull(-8, "账户不存在"),
    AccountDeleteError(-9, "账户注销失败"),
    AccountUpdateError(-10, "账户更新异常"),
    DeleteDataError(-11, "删除失败，数据不存在"),
    AddDataError(-12, "添加失败"),
    UpdateDataError(-13, "更新失败，数据不存在"),

    PhoneAlreadyHas(-23, "手机号已经被占用了"),
    DataParseError(-26, "数据解析错误"),

    // HTTP 状态码对应的错误码
    BadRequest(400, "请求参数错误"),
    Unauthorized(401, "未授权访问"),
    Forbidden(403, "权限不足"),
    NotFound(404, "资源不存在"),
    MethodNotAllowed(405, "请求方法不允许"),
    Conflict(409, "资源冲突"),
    InternalServerError(500, "服务器内部错误"),
    ServiceUnavailable(503, "服务不可用"),
    TooManyRequests(429, "请求频繁"),

    PhoneVerifyCodeError(301, "手机验证码错误或者验证码已过期"),

    // 系统相关错误码
    SYSTEM_ERROR(500, "系统错误"),
    PARAMS_ERROR(400, "参数错误"),
    NOT_LOGIN(401, "用户未登录"),
    NOT_LOGIN_ERROR(401, "用户未登录"),
    NO_AUTH_ERROR(403, "无权限访问"),
    TOO_MANY_REQUESTS(429, "请求频繁");


    private final int code;
    private final String message;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public static ErrorCode fromCode(int code) {
        for (ErrorCode value : values()) {
            if (value.code == code) {
                return value;
            }
        }
        return ServerError;
    }
}
