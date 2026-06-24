import request from './request'

// 后台权限/菜单 /api/admin/permission/**
export function getPermissionTree() {
  return request.get('/permission/tree')
}

export function listPermissions() {
  return request.get('/permission/list')
}
