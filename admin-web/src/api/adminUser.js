import request from './request'

// 后台管理员管理 /api/admin/admin-user/**
export function pageAdminUsers(params) {
  return request.get('/admin-user/page', { params })
}

export function getAdminUser(id) {
  return request.get(`/admin-user/${id}`)
}

export function saveAdminUser(data) {
  return request.post('/admin-user', data)
}

export function changeAdminUserStatus(id, status) {
  return request.post(`/admin-user/${id}/status`, null, { params: { status } })
}

// 重置为系统默认密码（无需传密码）
export function resetAdminUserPwd(id) {
  return request.post(`/admin-user/${id}/reset-pwd`)
}

export function deleteAdminUser(id) {
  return request.delete(`/admin-user/${id}`)
}
