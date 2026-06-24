import request from './request'

// 后台认证相关接口 /api/admin/auth/**
export function login(data) {
  return request.post('/auth/login', data)
}

export function logout() {
  return request.post('/auth/logout')
}

export function getInfo() {
  return request.get('/auth/info')
}

export function changePassword(data) {
  return request.post('/auth/change-password', data)
}
