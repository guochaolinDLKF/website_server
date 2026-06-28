package com.ydzz.admin.common;

import com.ydzz.common.ErrorCode;
import com.ydzz.exception.BusinessException;

import java.util.regex.Pattern;

/**
 * 后台账号密码强度规则（统一口径，供新增成员、重置密码、修改密码校验使用）。
 *
 * <p>规则：必须同时包含 数字、小写字母、大写字母、特殊符号，且长度大于 12 位。</p>
 *
 * @author WebsiteServer
 * @since 1.0.0
 */
public final class PasswordPolicy {

    /** 规则文案（与前端 {@code src/utils/password.js} 保持一致，前后端同口径展示/校验） */
    public static final String MESSAGE = "密码须包含数字、大小写字母、特殊符号，且长度大于12位";

    /** 数字 + 小写 + 大写 + 特殊符号(非字母数字) + 长度>12（即至少 13 位） */
    private static final Pattern PATTERN =
            Pattern.compile("^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[^A-Za-z0-9]).{13,}$");

    private PasswordPolicy() {
    }

    public static boolean isValid(String password) {
        return password != null && PATTERN.matcher(password).matches();
    }

    /** 校验密码，不符合规则时抛出业务异常。 */
    public static void check(String password) {
        if (!isValid(password)) {
            throw new BusinessException(ErrorCode.BadRequest, MESSAGE);
        }
    }
}
