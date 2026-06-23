package com.ydzz.admin.service;

import cn.hutool.crypto.digest.BCrypt;
import com.ydzz.admin.config.AdminStpUtil;
import com.ydzz.admin.dto.AdminInfoVo;
import com.ydzz.admin.dto.AdminLoginRequest;
import com.ydzz.admin.dto.AdminLoginVo;
import com.ydzz.admin.entity.AdminUser;
import com.ydzz.admin.mapper.AdminUserMapper;
import com.ydzz.common.ErrorCode;
import com.ydzz.exception.BusinessException;
import com.ydzz.util.ClientIpUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * 后台认证服务：登录、登出、当前信息、改密。
 *
 * @author WebsiteServer
 * @since 1.0.0
 */
@Slf4j
@Service
public class AdminAuthService {

    private final AdminUserMapper adminUserMapper;
    private final AdminUserService adminUserService;
    private final AdminRbacService rbacService;
    private final AdminPermissionService permissionService;
    private final AdminLogService logService;

    public AdminAuthService(AdminUserMapper adminUserMapper, AdminUserService adminUserService,
                            AdminRbacService rbacService, AdminPermissionService permissionService,
                            AdminLogService logService) {
        this.adminUserMapper = adminUserMapper;
        this.adminUserService = adminUserService;
        this.rbacService = rbacService;
        this.permissionService = permissionService;
        this.logService = logService;
    }

    public AdminLoginVo login(AdminLoginRequest req, HttpServletRequest request) {
        String ip = ClientIpUtil.getClientIp(request);
        String ua = request.getHeader("User-Agent");

        AdminUser user = adminUserService.getByUsername(req.getUsername());
        if (user == null || !BCrypt.checkpw(req.getPassword(), user.getPassword())) {
            logService.saveLoginLog(user == null ? null : user.getId(), req.getUsername(), ip, ua,
                    1, 0, "账号或密码错误");
            throw new BusinessException(ErrorCode.Unauthorized, "账号或密码错误");
        }
        if (user.getStatus() != null && user.getStatus() == 0) {
            logService.saveLoginLog(user.getId(), user.getUsername(), ip, ua, 1, 0, "账号已被禁用");
            throw new BusinessException(ErrorCode.Forbidden, "账号已被禁用");
        }

        // 登录后台独立域
        AdminStpUtil.login(user.getId());

        // 更新最后登录信息
        AdminUser update = new AdminUser();
        update.setId(user.getId());
        update.setLastLoginTime(LocalDateTime.now());
        update.setLastLoginIp(ip);
        adminUserMapper.updateById(update);

        logService.saveLoginLog(user.getId(), user.getUsername(), ip, ua, 1, 1, "登录成功");

        AdminLoginVo vo = new AdminLoginVo();
        vo.setTokenName(AdminStpUtil.getTokenInfo().getTokenName());
        vo.setTokenValue(AdminStpUtil.getTokenValue());
        vo.setAdminInfo(buildInfo(user));
        return vo;
    }

    public void logout(HttpServletRequest request) {
        Object loginId = AdminStpUtil.getLoginIdDefaultNull();
        if (loginId != null) {
            AdminUser user = adminUserMapper.selectById(Long.valueOf(loginId.toString()));
            String username = user == null ? null : user.getUsername();
            logService.saveLoginLog(user == null ? null : user.getId(), username,
                    ClientIpUtil.getClientIp(request), request.getHeader("User-Agent"),
                    2, 1, "登出成功");
        }
        AdminStpUtil.logout();
    }

    public AdminInfoVo currentInfo() {
        long adminId = AdminStpUtil.getLoginIdAsLong();
        AdminUser user = adminUserMapper.selectById(adminId);
        if (user == null) {
            throw new BusinessException(ErrorCode.NotFound, "管理员不存在");
        }
        return buildInfo(user);
    }

    public void changePassword(String oldPassword, String newPassword) {
        long adminId = AdminStpUtil.getLoginIdAsLong();
        adminUserService.changePassword(adminId, oldPassword, newPassword);
    }

    private AdminInfoVo buildInfo(AdminUser user) {
        AdminInfoVo info = new AdminInfoVo();
        info.setId(user.getId());
        info.setUsername(user.getUsername());
        info.setRealName(user.getRealName());
        info.setAvatar(user.getAvatar());
        info.setRoles(rbacService.listRoleCodes(user.getId()));
        info.setPermissions(rbacService.listPermissionCodes(user.getId()));
        info.setMenus(permissionService.buildTree(rbacService.listPermissions(user.getId())));
        return info;
    }
}
