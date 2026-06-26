import request from './request'

// 数据驾驶舱 /api/admin/dashboard/**
export function getOverview() {
  return request.get('/dashboard/overview')
}

// 玩家概览卡片：今日（新增/活跃/付费玩家/付费金额，含环比同比）+ 当月累计
export function getPlayerStats() {
  return request.get('/dashboard/player-stats')
}

// 付费概况：付费金额/付费人数/付费率/ARPU，各含日环比、周同比
export function getPayOverview() {
  return request.get('/dashboard/pay-overview')
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

// 实时新增用户：{ date|start|end, bucket, cumulative: 0|1 }
export function getRealtimeNewUsers(params = {}) {
  return request.get('/dashboard/realtime-new-users', { params })
}

// 新增用户趋势（多日）：{ dim, start, end, cumulative: 0|1 }
export function getNewUsersTrend(params = { dim: 'day' }) {
  return request.get('/dashboard/new-users-trend', { params })
}

// 实时付费金额：{ date|start|end, bucket, cumulative: 0|1 }
export function getRealtimeRevenue(params = {}) {
  return request.get('/dashboard/realtime-revenue', { params })
}

// 付费金额趋势（多日）：{ dim, start, end, cumulative: 0|1 }
export function getRevenueTrend(params = { dim: 'day' }) {
  return request.get('/dashboard/revenue-trend', { params })
}

// 新增玩家数量及占比（柱=新增玩家，线=新增/活跃占比%）：{ dim, start, end }
export function getNewPlayersTrend(params = { dim: 'day' }) {
  return request.get('/dashboard/new-players-trend', { params })
}

// 新增设备数量及占比（柱=激活设备，线=新增/累计设备占比%）：{ dim, start, end }
export function getNewDevicesTrend(params = { dim: 'day' }) {
  return request.get('/dashboard/new-devices-trend', { params })
}

// 各渠道新增玩家（返回 labels/channels/series，前端渲染堆积图与占比图）：{ dim, start, end }
export function getChannelNewPlayers(params = { dim: 'day' }) {
  return request.get('/dashboard/channel-new-players', { params })
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
