package com.ydzz.admin.config;

import cn.hutool.crypto.digest.BCrypt;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ydzz.admin.entity.AdminPermission;
import com.ydzz.admin.entity.AdminRole;
import com.ydzz.admin.entity.AdminUser;
import com.ydzz.admin.entity.AdminUserRole;
import com.ydzz.admin.mapper.AdminPermissionMapper;
import com.ydzz.admin.mapper.AdminRoleMapper;
import com.ydzz.admin.mapper.AdminRolePermissionMapper;
import com.ydzz.admin.mapper.AdminUserMapper;
import com.ydzz.admin.mapper.AdminUserRoleMapper;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * 后台初始化数据：启动时确保存在默认超级管理员账号（幂等）。
 *
 * <p>密码以 BCrypt 安全写入，不在 SQL 中保存明文。若后台表尚未建立
 * （admin_init.sql 未执行），将打印告警并跳过，不影响应用启动。</p>
 *
 * @author WebsiteServer
 * @since 1.0.0
 */
@Slf4j
@Order(100)
@Component
public class AdminDataInitializer implements ApplicationRunner {

    /** 默认超级管理员账号与初始密码（首次登录后请尽快修改） */
    private static final String DEFAULT_USERNAME = "admin";
    private static final String DEFAULT_PASSWORD = "Admin@123";
    private static final String SUPER_ADMIN_ROLE = "SUPER_ADMIN";

    /** 已下线、需在启动时清理的权限码（移除对应菜单及其角色授权） */
    private static final List<String> RETIRED_PERMISSION_CODES =
            List.of("func:config", "content:menu", "banner:list", "notice:list");

    private final AdminUserMapper adminUserMapper;
    private final AdminRoleMapper adminRoleMapper;
    private final AdminUserRoleMapper adminUserRoleMapper;
    private final AdminPermissionMapper adminPermissionMapper;
    private final AdminRolePermissionMapper adminRolePermissionMapper;

    public AdminDataInitializer(AdminUserMapper adminUserMapper, AdminRoleMapper adminRoleMapper,
                                AdminUserRoleMapper adminUserRoleMapper, AdminPermissionMapper adminPermissionMapper,
                                AdminRolePermissionMapper adminRolePermissionMapper) {
        this.adminUserMapper = adminUserMapper;
        this.adminRoleMapper = adminRoleMapper;
        this.adminUserRoleMapper = adminUserRoleMapper;
        this.adminPermissionMapper = adminPermissionMapper;
        this.adminRolePermissionMapper = adminRolePermissionMapper;
    }

    @Override
    public void run(ApplicationArguments args) {
        cleanRetiredPermissions();
        ensurePermissionNames();
        try {
            LambdaQueryWrapper<AdminUser> qw = new LambdaQueryWrapper<>();
            qw.eq(AdminUser::getUsername, DEFAULT_USERNAME);
            if (adminUserMapper.selectCount(qw) > 0) {
                log.info("[后台初始化] 超级管理员账号已存在，跳过初始化");
                return;
            }

            AdminUser admin = new AdminUser();
            admin.setUsername(DEFAULT_USERNAME);
            admin.setPassword(BCrypt.hashpw(DEFAULT_PASSWORD));
            admin.setRealName("超级管理员");
            admin.setStatus(1);
            admin.setRemark("系统初始化创建");
            adminUserMapper.insert(admin);

            // 绑定 SUPER_ADMIN 角色
            LambdaQueryWrapper<AdminRole> roleQw = new LambdaQueryWrapper<>();
            roleQw.eq(AdminRole::getRoleCode, SUPER_ADMIN_ROLE);
            AdminRole role = adminRoleMapper.selectOne(roleQw);
            if (role != null) {
                AdminUserRole ur = new AdminUserRole();
                ur.setAdminId(admin.getId());
                ur.setRoleId(role.getId());
                adminUserRoleMapper.insert(ur);
            } else {
                log.warn("[后台初始化] 未找到 SUPER_ADMIN 角色，请先执行 admin_init.sql 中的角色初始化数据");
            }

            log.info("========================================");
            log.info("  [后台初始化] 已创建默认超级管理员");
            log.info("  账号: {}  初始密码: {}", DEFAULT_USERNAME, DEFAULT_PASSWORD);
            log.info("  ⚠ 请首次登录后立即修改密码！");
            log.info("========================================");
        } catch (Exception e) {
            log.warn("[后台初始化] 跳过（后台表可能尚未建立，请先执行 sql/admin_init.sql）：{}", e.getMessage());
        }
    }

    /**
     * 启动时校正菜单显示名（幂等）：如「数据驾驶舱」更名为「主控制台」。
     */
    private void ensurePermissionNames() {
        renamePermission("dashboard:view", "主控制台");
    }

    private void renamePermission(String code, String newName) {
        try {
            AdminPermission perm = adminPermissionMapper.selectOne(
                    new LambdaQueryWrapper<AdminPermission>().eq(AdminPermission::getPermissionCode, code));
            if (perm != null && !newName.equals(perm.getPermissionName())) {
                AdminPermission update = new AdminPermission();
                update.setId(perm.getId());
                update.setPermissionName(newName);
                adminPermissionMapper.updateById(update);
                log.info("[后台初始化] 菜单更名：{} -> {}", code, newName);
            }
        } catch (Exception e) {
            log.warn("[后台初始化] 菜单更名跳过（{}）：{}", code, e.getMessage());
        }
    }

    /**
     * 启动时清理已下线的菜单/权限（幂等）：删除权限本身及其角色授权，使下线的菜单不再出现。
     */
    private void cleanRetiredPermissions() {
        try {
            List<AdminPermission> retired = adminPermissionMapper.selectList(
                    new LambdaQueryWrapper<AdminPermission>().in(AdminPermission::getPermissionCode, RETIRED_PERMISSION_CODES));
            for (AdminPermission perm : retired) {
                adminRolePermissionMapper.delete(
                        new LambdaQueryWrapper<com.ydzz.admin.entity.AdminRolePermission>()
                                .eq(com.ydzz.admin.entity.AdminRolePermission::getPermissionId, perm.getId()));
                adminPermissionMapper.deleteById(perm.getId());
                log.info("[后台初始化] 已移除下线菜单：{}({})", perm.getPermissionName(), perm.getPermissionCode());
            }
        } catch (Exception e) {
            log.warn("[后台初始化] 清理下线菜单跳过：{}", e.getMessage());
        }
    }
}
