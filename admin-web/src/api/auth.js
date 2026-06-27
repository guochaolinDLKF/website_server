import request from './request'

// 后台认证相关接口 /api/admin/auth/**
export function login(data) {
  return request.post('/auth/login', data)
}

// 获取图形验证码：返回 { captchaId, image(base64 data url) }
export function getCaptcha() {
  return request.get('/auth/captcha')
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
