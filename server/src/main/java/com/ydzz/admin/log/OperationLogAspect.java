package com.ydzz.admin.log;

import com.alibaba.fastjson2.JSON;
import com.ydzz.admin.config.AdminStpUtil;
import com.ydzz.admin.entity.AdminOperationLog;
import com.ydzz.admin.entity.AdminUser;
import com.ydzz.admin.mapper.AdminUserMapper;
import com.ydzz.admin.service.AdminLogService;
import com.ydzz.admin.service.IpRegionService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * 后台操作日志切面：环绕 {@link OperationLog} 注解的方法记录审计信息。
 *
 * @author WebsiteServer
 * @since 1.0.0
 */
@Slf4j
@Aspect
@Component
public class OperationLogAspect {

    private static final int MAX_TEXT = 4000;

    private final AdminLogService logService;
    private final AdminUserMapper adminUserMapper;
    private final IpRegionService ipRegionService;

    public OperationLogAspect(AdminLogService logService, AdminUserMapper adminUserMapper,
                              IpRegionService ipRegionService) {
        this.logService = logService;
        this.adminUserMapper = adminUserMapper;
        this.ipRegionService = ipRegionService;
    }

    @Around("@annotation(operationLog)")
    public Object around(ProceedingJoinPoint joinPoint, OperationLog operationLog) throws Throwable {
        long start = System.currentTimeMillis();
        AdminOperationLog logEntity = new AdminOperationLog();
        logEntity.setModule(operationLog.module());
        logEntity.setOperation(operationLog.operation());
        logEntity.setCreateTime(LocalDateTime.now());

        // 当前管理员
        try {
            Object loginId = AdminStpUtil.getLoginIdDefaultNull();
            if (loginId != null) {
                Long adminId = Long.valueOf(loginId.toString());
                logEntity.setAdminId(adminId);
                AdminUser user = adminUserMapper.selectById(adminId);
                if (user != null) {
                    logEntity.setUsername(user.getUsername());
                }
            }
        } catch (Exception ignore) {
        }

        // 请求信息
        HttpServletRequest request = currentRequest();
        if (request != null) {
            logEntity.setRequestUri(request.getRequestURI());
            logEntity.setRequestMethod(request.getMethod());
            logEntity.setIp(ipRegionService.resolvePublicIp(request));
        }

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        logEntity.setMethod(signature.getDeclaringTypeName() + "." + signature.getName());

        if (operationLog.saveParam()) {
            logEntity.setRequestParam(truncate(toJson(joinPoint.getArgs())));
        }

        try {
            Object result = joinPoint.proceed();
            logEntity.setStatus(1);
            if (operationLog.saveResult()) {
                logEntity.setResponseResult(truncate(toJson(result)));
            }
            return result;
        } catch (Throwable ex) {
            logEntity.setStatus(0);
            logEntity.setErrorMsg(truncate(ex.getMessage()));
            throw ex;
        } finally {
            logEntity.setCostMs(System.currentTimeMillis() - start);
            logService.saveOperationLog(logEntity);
        }
    }

    private HttpServletRequest currentRequest() {
        try {
            ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            return attrs == null ? null : attrs.getRequest();
        } catch (Exception e) {
            return null;
        }
    }

    /** 序列化参数，过滤 Servlet/文件等不可序列化对象，并屏蔽敏感字段 */
    private String toJson(Object obj) {
        try {
            if (obj instanceof Object[] args) {
                Object[] filtered = Arrays.stream(args)
                        .filter(a -> !(a instanceof HttpServletRequest)
                                && !(a instanceof HttpServletResponse)
                                && !(a instanceof MultipartFile))
                        .toArray();
                String json = JSON.toJSONString(filtered);
                return maskSensitive(json);
            }
            return maskSensitive(JSON.toJSONString(obj));
        } catch (Exception e) {
            return Arrays.stream(new Object[]{obj}).map(String::valueOf).collect(Collectors.joining());
        }
    }

    private String maskSensitive(String json) {
        if (json == null) {
            return null;
        }
        // 简单屏蔽 password/newPassword/oldPassword 字段值
        return json.replaceAll("(\"(?:old|new)?[Pp]assword\"\\s*:\\s*\")[^\"]*(\")", "$1******$2");
    }

    private String truncate(String text) {
        if (text == null) {
            return null;
        }
        return text.length() > MAX_TEXT ? text.substring(0, MAX_TEXT) : text;
    }
}
