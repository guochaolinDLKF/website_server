import request from './request'

// 后台日志 /api/admin/log/**
export function pageLoginLog(params) {
  return request.get('/log/login', { params })
}

export function pageOperationLog(params) {
  return request.get('/log/operation', { params })
}
