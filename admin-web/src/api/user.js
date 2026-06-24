import request from './request'

// APP 用户管理 /api/admin/user/**
export function pageUsers(params) {
  return request.get('/user/page', { params })
}

export function getUserDetail(id) {
  return request.get(`/user/${id}`)
}

export function changeUserStatus(id, status) {
  return request.post(`/user/${id}/status`, null, { params: { status } })
}

export function exportUsers(params) {
  return request.get('/user/export', { params })
}
