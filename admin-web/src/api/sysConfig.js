import request from './request'

// 系统配置 /api/admin/sys-config/**
export function pageConfig(params) {
  return request.get('/sys-config/page', { params })
}

export function saveConfig(data) {
  return request.post('/sys-config', data)
}

export function deleteConfig(id) {
  return request.delete(`/sys-config/${id}`)
}
