package com.ydzz.admin.service;

import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.BCrypt;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ydzz.admin.config.AdminStpUtil;
import com.ydzz.admin.dto.AdminUserSaveRequest;
import com.ydzz.admin.entity.AdminRole;
import com.ydzz.admin.entity.AdminUser;
import com.ydzz.admin.entity.AdminUserRole;
import com.ydzz.admin.mapper.AdminRoleMapper;
import com.ydzz.admin.mapper.AdminUserMapper;
import com.ydzz.admin.mapper.AdminUserRoleMapper;
import com.ydzz.common.ErrorCode;
import com.ydzz.exception.BusinessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 后台管理员服务：CRUD、角色绑定、密码管理。
 *
 * @author WebsiteServer
 * @since 1.0.0
 */
@Service
public class AdminUserService {

    /** 普通成员角色编码：新增账号默认绑定此角色 */
    private static final String MEMBER_ROLE_CODE = "MEMBER";
    /** 超级管理员角色编码：禁止禁用/删除拥有该角色的账号 */
    private static final String SUPER_ADMIN_ROLE_CODE = "SUPER_ADMIN";

    private final AdminUserMapper adminUserMapper;
    private final AdminUserRoleMapper userRoleMapper;
    private final AdminRoleMapper roleMapper;
    private final AdminRbacService rbacService;

    public AdminUserService(AdminUserMapper adminUserMapper, AdminUserRoleMapper userRoleMapper,
                            AdminRoleMapper roleMapper, AdminRbacService rbacService) {
        this.adminUserMapper = adminUserMapper;
        this.userRoleMapper = userRoleMapper;
        this.roleMapper = roleMapper;
        this.rbacService = rbacService;
    }

    public AdminUser getByUsername(String username) {
        LambdaQueryWrapper<AdminUser> qw = new LambdaQueryWrapper<>();
        qw.eq(AdminUser::getUsername, username);
        return adminUserMapper.selectOne(qw);
    }

    public AdminUser getById(Long id) {
        return adminUserMapper.selectById(id);
    }

    public Page<AdminUser> page(long current, long size, String keyword, Integer status) {
        LambdaQueryWrapper<AdminUser> qw = new LambdaQueryWrapper<>();
        if (StrUtil.isNotBlank(keyword)) {
            qw.and(w -> w.like(AdminUser::getUsername, keyword)
                    .or().like(AdminUser::getRealName, keyword)
                    .or().like(AdminUser::getPhone, keyword));
        }
        if (status != null) {
            qw.eq(AdminUser::getStatus, status);
        }
        qw.orderByDesc(AdminUser::getCreateTime);
        return adminUserMapper.selectPage(new Page<>(current, size), qw);
    }

    /** 查询管理员已绑定的角色ID */
    public List<Long> listRoleIds(Long adminId) {
        LambdaQueryWrapper<AdminUserRole> qw = new LambdaQueryWrapper<>();
        qw.eq(AdminUserRole::getAdminId, adminId);
        return userRoleMapper.selectList(qw).stream().map(AdminUserRole::getRoleId).toList();
    }

    @Transactional(rollbackFor = Exception.class)
    public Long save(AdminUserSaveRequest req) {
        AdminUser entity = new AdminUser();
        entity.setUsername(req.getUsername());
        entity.setRealName(req.getRealName());
        entity.setPhone(req.getPhone());
        entity.setEmail(req.getEmail());
        entity.setStatus(req.getStatus() == null ? 1 : req.getStatus());
        entity.setRemark(req.getRemark());

        if (req.getId() == null) {
            // 新增：账号唯一校验 + 密码必填
            if (getByUsername(req.getUsername()) != null) {
                throw new BusinessException(ErrorCode.Conflict, "账号已存在");
            }
            if (StrUtil.isBlank(req.getPassword())) {
                throw new BusinessException(ErrorCode.BadRequest, "新增管理员必须设置密码");
            }
            entity.setPassword(BCrypt.hashpw(req.getPassword()));
            adminUserMapper.insert(entity);
        } else {
            AdminUser exist = adminUserMapper.selectById(req.getId());
            if (exist == null) {
                throw new BusinessException(ErrorCode.NotFound, "管理员不存在");
            }
            entity.setId(req.getId());
            // 密码留空表示不修改
            if (StrUtil.isNotBlank(req.getPassword())) {
                entity.setPassword(BCrypt.hashpw(req.getPassword()));
            }
            adminUserMapper.updateById(entity);
        }

        // 重新绑定角色：新增账号若未指定角色，系统默认绑定「普通成员(MEMBER)」
        List<Long> roleIds = req.getRoleIds();
        if (req.getId() == null && (roleIds == null || roleIds.isEmpty())) {
            Long memberRoleId = resolveMemberRoleId();
            if (memberRoleId != null) {
                roleIds = List.of(memberRoleId);
            }
        }
        if (roleIds != null) {
            rebindRoles(entity.getId(), roleIds);
        }
        return entity.getId();
    }

    /** 查询「普通成员(MEMBER)」角色ID，供新增账号默认绑定 */
    private Long resolveMemberRoleId() {
        LambdaQueryWrapper<AdminRole> qw = new LambdaQueryWrapper<>();
        qw.eq(AdminRole::getRoleCode, MEMBER_ROLE_CODE);
        AdminRole role = roleMapper.selectOne(qw);
        return role == null ? null : role.getId();
    }

    /** 是否为超级管理员账号 */
    private boolean isSuperAdmin(Long adminId) {
        return adminId != null && rbacService.listRoleCodes(adminId).contains(SUPER_ADMIN_ROLE_CODE);
    }

    @Transactional(rollbackFor = Exception.class)
    public void rebindRoles(Long adminId, List<Long> roleIds) {
        LambdaQueryWrapper<AdminUserRole> qw = new LambdaQueryWrapper<>();
        qw.eq(AdminUserRole::getAdminId, adminId);
        userRoleMapper.delete(qw);
        if (roleIds != null) {
            for (Long roleId : roleIds) {
                AdminUserRole ur = new AdminUserRole();
                ur.setAdminId(adminId);
                ur.setRoleId(roleId);
                userRoleMapper.insert(ur);
            }
        }
    }

    public void changeStatus(Long id, Integer status) {
        AdminUser exist = adminUserMapper.selectById(id);
        if (exist == null) {
            throw new BusinessException(ErrorCode.NotFound, "管理员不存在");
        }
        // 禁用时的兜底保护：不能禁用当前登录账号，超级管理员账号也不可被禁用
        if (status != null && status == 0) {
            long current = AdminStpUtil.getLoginIdAsLong();
            if (current == id) {
                throw new BusinessException(ErrorCode.BadRequest, "不能禁用当前登录账号");
            }
            if (isSuperAdmin(id)) {
                throw new BusinessException(ErrorCode.BadRequest, "超级管理员账号不可禁用");
            }
        }
        AdminUser update = new AdminUser();
        update.setId(id);
        update.setStatus(status);
        adminUserMapper.updateById(update);
    }

    public void resetPassword(Long id, String newPassword) {
        if (StrUtil.isBlank(newPassword)) {
            throw new BusinessException(ErrorCode.BadRequest, "新密码不能为空");
        }
        AdminUser exist = adminUserMapper.selectById(id);
        if (exist == null) {
            throw new BusinessException(ErrorCode.NotFound, "管理员不存在");
        }
        AdminUser update = new AdminUser();
        update.setId(id);
        update.setPassword(BCrypt.hashpw(newPassword));
        adminUserMapper.updateById(update);
    }

    public void changePassword(Long adminId, String oldPassword, String newPassword) {
        AdminUser exist = adminUserMapper.selectById(adminId);
        if (exist == null) {
            throw new BusinessException(ErrorCode.NotFound, "管理员不存在");
        }
        if (!BCrypt.checkpw(oldPassword, exist.getPassword())) {
            throw new BusinessException(ErrorCode.BadRequest, "原密码不正确");
        }
        AdminUser update = new AdminUser();
        update.setId(adminId);
        update.setPassword(BCrypt.hashpw(newPassword));
        adminUserMapper.updateById(update);
    }

    public void removeById(Long id) {
        // 兜底保护：不能删除当前登录账号，超级管理员账号也不可被删除
        long current = AdminStpUtil.getLoginIdAsLong();
        if (id != null && current == id) {
            throw new BusinessException(ErrorCode.BadRequest, "不能删除当前登录账号");
        }
        if (isSuperAdmin(id)) {
            throw new BusinessException(ErrorCode.BadRequest, "超级管理员账号不可删除");
        }
        adminUserMapper.deleteById(id);
    }
}
