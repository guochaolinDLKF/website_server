package com.ydzz.admin.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ydzz.admin.dto.AdminRoleSaveRequest;
import com.ydzz.admin.entity.AdminRole;
import com.ydzz.admin.entity.AdminRolePermission;
import com.ydzz.admin.mapper.AdminRoleMapper;
import com.ydzz.admin.mapper.AdminRolePermissionMapper;
import com.ydzz.common.ErrorCode;
import com.ydzz.exception.BusinessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 后台角色服务：CRUD、权限分配。
 *
 * @author WebsiteServer
 * @since 1.0.0
 */
@Service
public class AdminRoleService {

    private final AdminRoleMapper roleMapper;
    private final AdminRolePermissionMapper rolePermissionMapper;

    public AdminRoleService(AdminRoleMapper roleMapper, AdminRolePermissionMapper rolePermissionMapper) {
        this.roleMapper = roleMapper;
        this.rolePermissionMapper = rolePermissionMapper;
    }

    public List<AdminRole> listAll() {
        LambdaQueryWrapper<AdminRole> qw = new LambdaQueryWrapper<>();
        qw.orderByAsc(AdminRole::getSortOrder);
        return roleMapper.selectList(qw);
    }

    public Long save(AdminRoleSaveRequest req) {
        AdminRole entity = new AdminRole();
        entity.setRoleCode(req.getRoleCode());
        entity.setRoleName(req.getRoleName());
        entity.setDescription(req.getDescription());
        entity.setStatus(req.getStatus() == null ? 1 : req.getStatus());
        entity.setSortOrder(req.getSortOrder() == null ? 0 : req.getSortOrder());
        if (req.getId() == null) {
            LambdaQueryWrapper<AdminRole> qw = new LambdaQueryWrapper<>();
            qw.eq(AdminRole::getRoleCode, req.getRoleCode());
            if (roleMapper.selectCount(qw) > 0) {
                throw new BusinessException(ErrorCode.Conflict, "角色编码已存在");
            }
            roleMapper.insert(entity);
        } else {
            entity.setId(req.getId());
            roleMapper.updateById(entity);
        }
        return entity.getId();
    }

    public void changeStatus(Long id, Integer status) {
        AdminRole update = new AdminRole();
        update.setId(id);
        update.setStatus(status);
        roleMapper.updateById(update);
    }

    public void removeById(Long id) {
        roleMapper.deleteById(id);
    }

    /** 查询角色已分配的权限ID */
    public List<Long> listPermissionIds(Long roleId) {
        LambdaQueryWrapper<AdminRolePermission> qw = new LambdaQueryWrapper<>();
        qw.eq(AdminRolePermission::getRoleId, roleId);
        return rolePermissionMapper.selectList(qw).stream()
                .map(AdminRolePermission::getPermissionId).toList();
    }

    /** 给角色分配权限（全量覆盖） */
    @Transactional(rollbackFor = Exception.class)
    public void assignPermissions(Long roleId, List<Long> permissionIds) {
        if (roleMapper.selectById(roleId) == null) {
            throw new BusinessException(ErrorCode.NotFound, "角色不存在");
        }
        LambdaQueryWrapper<AdminRolePermission> qw = new LambdaQueryWrapper<>();
        qw.eq(AdminRolePermission::getRoleId, roleId);
        rolePermissionMapper.delete(qw);
        if (permissionIds != null) {
            for (Long pid : permissionIds) {
                AdminRolePermission rp = new AdminRolePermission();
                rp.setRoleId(roleId);
                rp.setPermissionId(pid);
                rolePermissionMapper.insert(rp);
            }
        }
    }
}
