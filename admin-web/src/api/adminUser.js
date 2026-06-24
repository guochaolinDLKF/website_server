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

export function resetAdminUserPwd(id, newPassword) {
  return request.post(`/admin-user/${id}/reset-pwd`, null, { params: { newPassword } })
}

export function deleteAdminUser(id) {
  return request.delete(`/admin-user/${id}`)
}
