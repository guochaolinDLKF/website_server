import request from './request'

// 业务数据（只读 zhouyi）：订单 / 商品 / 权益 / 支付异常

// 订单
export function pageOrders(params) {
  return request.get('/order/page', { params })
}
export function getOrderDetail(id) {
  return request.get(`/order/${id}`)
}

// 商品
export function pageGoods(params) {
  return request.get('/goods/page', { params })
}
// 新增/编辑商品（后端限超级管理员/管理员）
export function saveGoods(data) {
  return request.post('/goods', data)
}
// 删除商品（后端限超级管理员/管理员）
export function deleteGoods(id) {
  return request.delete(`/goods/${id}`)
}

// 权益
export function pageBenefits(params) {
  return request.get('/benefit/page', { params })
}

// 支付异常
export function pagePaymentFailures(params) {
  return request.get('/payment-failure/page', { params })
}
