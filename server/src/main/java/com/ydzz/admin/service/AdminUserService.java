package com.ydzz.admin.service;

import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.BCrypt;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ydzz.admin.dto.AdminUserSaveRequest;
import com.ydzz.admin.entity.AdminUser;
import com.ydzz.admin.entity.AdminUserRole;
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

    private final AdminUserMapper adminUserMapper;
    private final AdminUserRoleMapper userRoleMapper;

    public AdminUserService(AdminUserMapper adminUserMapper, AdminUserRoleMapper userRoleMapper) {
        this.adminUserMapper = adminUserMapper;
        this.userRoleMapper = userRoleMapper;
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

        // 重新绑定角色
        if (req.getRoleIds() != null) {
            rebindRoles(entity.getId(), req.getRoleIds());
        }
        return entity.getId();
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
        adminUserMapper.deleteById(id);
    }
}
