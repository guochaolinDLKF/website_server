import request from './request'

// 后台角色管理 /api/admin/role/**
export function listRoles() {
  return request.get('/role/list')
}

export function saveRole(data) {
  return request.post('/role', data)
}

export function changeRoleStatus(id, status) {
  return request.post(`/role/${id}/status`, null, { params: { status } })
}

export function deleteRole(id) {
  return request.delete(`/role/${id}`)
}

export function getRolePermissions(id) {
  return request.get(`/role/${id}/permissions`)
}

export function assignRolePermissions(id, permissionIds) {
  return request.post(`/role/${id}/permissions`, permissionIds)
}
