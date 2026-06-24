import request from './request'

// 数据驾驶舱 /api/admin/dashboard/**
export function getOverview() {
  return request.get('/dashboard/overview')
}

// 玩家概览卡片：今日（新增/活跃/付费玩家/付费金额，含环比同比）+ 当月累计
export function getPlayerStats() {
  return request.get('/dashboard/player-stats')
}

export function getUserTrend(days = 30) {
  return request.get('/dashboard/user-trend', { params: { days } })
}

// 活跃数据：type = hour(今日每小时) | week(过去7天) | month(本月)
export function getActiveData(type = 'week') {
  return request.get('/dashboard/active', { params: { type } })
}

// 新增用户次日留存：传 { start, end }(yyyy-MM-dd 注册日区间) 或 { days }(最近N个注册日)
export function getRetention(params = { days: 30 }) {
  return request.get('/dashboard/retention', { params })
}

// 人均在线时长/启动次数：传 { days } 或 { start, end }，返回 { duration, launch }
export function getOnlineStats(params = { days: 30 }) {
  return request.get('/dashboard/online-stats', { params })
}

// 实时在线人数（按5分钟，今日+昨日VS）：可选 { date: 'yyyy-MM-dd', bucket: 1|5|10|60 }
export function getRealtimeOnline(params = {}) {
  return request.get('/dashboard/realtime-online', { params })
}

// 日均在线人数趋势（多日）：{ dim: 'day'|'week'|'month' }
export function getOnlineTrend(params = { dim: 'day' }) {
  return request.get('/dashboard/online-trend', { params })
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
