import request from './request'

// 数据驾驶舱 /api/admin/dashboard/**
export function getOverview() {
  return request.get('/dashboard/overview')
}

export function getUserTrend(days = 30) {
  return request.get('/dashboard/user-trend', { params: { days } })
}

// 活跃数据：type = hour(今日每小时) | week(过去7天) | month(本月)
export function getActiveData(type = 'week') {
  return request.get('/dashboard/active', { params: { type } })
}

export function getIncomeTrend(days = 30) {
  return request.get('/dashboard/income-trend', { params: { days } })
}

export function getOrderTrend(days = 30) {
  return request.get('/dashboard/order-trend', { params: { days } })
}

export function getGoodsRank() {
  return request.get('/dashboard/goods-rank')
}

export function getChannelDist() {
  return request.get('/dashboard/channel-dist')
}

export function getBenefitDist() {
  return request.get('/dashboard/benefit-dist')
}
