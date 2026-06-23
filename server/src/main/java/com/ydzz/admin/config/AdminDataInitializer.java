package com.ydzz.admin.config;

import cn.hutool.crypto.digest.BCrypt;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ydzz.admin.entity.AdminRole;
import com.ydzz.admin.entity.AdminUser;
import com.ydzz.admin.entity.AdminUserRole;
import com.ydzz.admin.mapper.AdminRoleMapper;
import com.ydzz.admin.mapper.AdminUserMapper;
import com.ydzz.admin.mapper.AdminUserRoleMapper;
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

    private final AdminUserMapper adminUserMapper;
    private final AdminRoleMapper adminRoleMapper;
    private final AdminUserRoleMapper adminUserRoleMapper;

    public AdminDataInitializer(AdminUserMapper adminUserMapper, AdminRoleMapper adminRoleMapper,
                                AdminUserRoleMapper adminUserRoleMapper) {
        this.adminUserMapper = adminUserMapper;
        this.adminRoleMapper = adminRoleMapper;
        this.adminUserRoleMapper = adminUserRoleMapper;
    }

    @Override
    public void run(ApplicationArguments args) {
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
}
