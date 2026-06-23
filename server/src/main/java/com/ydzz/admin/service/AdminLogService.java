package com.ydzz.admin.service;

import com.ydzz.admin.entity.AdminLoginLog;
import com.ydzz.admin.entity.AdminOperationLog;
import com.ydzz.admin.mapper.AdminLoginLogMapper;
import com.ydzz.admin.mapper.AdminOperationLogMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * 后台日志服务：登录日志与操作日志落库（异步，失败不影响主流程）。
 *
 * @author WebsiteServer
 * @since 1.0.0
 */
@Slf4j
@Service
public class AdminLogService {

    private final AdminLoginLogMapper loginLogMapper;
    private final AdminOperationLogMapper operationLogMapper;

    public AdminLogService(AdminLoginLogMapper loginLogMapper, AdminOperationLogMapper operationLogMapper) {
        this.loginLogMapper = loginLogMapper;
        this.operationLogMapper = operationLogMapper;
    }

    /**
     * 记录登录/登出日志
     *
     * @param loginType 1登录 2登出
     * @param status    1成功 0失败
     */
    @Async
    public void saveLoginLog(Long adminId, String username, String ip, String userAgent,
                             int loginType, int status, String msg) {
        try {
            AdminLoginLog logEntity = new AdminLoginLog();
            logEntity.setAdminId(adminId);
            logEntity.setUsername(username);
            logEntity.setLoginIp(ip);
            logEntity.setUserAgent(userAgent);
            logEntity.setLoginType(loginType);
            logEntity.setStatus(status);
            logEntity.setMsg(msg);
            logEntity.setLoginTime(LocalDateTime.now());
            loginLogMapper.insert(logEntity);
        } catch (Exception e) {
            log.warn("[后台登录日志] 写入失败: {}", e.getMessage());
        }
    }

    @Async
    public void saveOperationLog(AdminOperationLog logEntity) {
        try {
            operationLogMapper.insert(logEntity);
        } catch (Exception e) {
            log.warn("[后台操作日志] 写入失败: {}", e.getMessage());
        }
    }
}
