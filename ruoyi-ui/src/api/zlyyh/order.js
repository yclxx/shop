import request from '@/utils/request'

// 查询订单列表
export function listOrder(query) {
  return request({
    url: '/zlyyh-admin/order/list',
    method: 'get',
    params: query
  })
}

// 查询订单详细
export function getOrder(number) {
  return request({
    url: '/zlyyh-admin/order/' + number,
    method: 'get'
  })
}

// 新增订单
export function addOrder(data) {
  return request({
    url: '/zlyyh-admin/order',
    method: 'post',
    data: data
  })
}

// 修改订单
export function updateOrder(data) {
  return request({
    url: '/zlyyh-admin/order',
    method: 'put',
    data: data
  })
}

// 删除订单
export function delOrder(number) {
  return request({
    url: '/zlyyh-admin/order/' + number,
    method: 'delete'
  })
}

// 新增订单
export function exportOrder(data) {
  return request({
    url: '/zlyyh-admin/order/exportOrder',
    method: 'post',
    data: data
  })
}

// 订单补发
export function orderReissue(number) {
  return request({
    url: '/zlyyh-admin/order/orderReissue/' + number,
    method: 'get'
  })
}

// 美食退款
export function cancelFoodOrder(number) {
  return request({
    url: '/zlyyh-admin/order/cancelFoodOrder/' + number,
    method: 'get'
  })
}

// 查询用户信息列表
export function syncOrderData(query) {
  return request({
    url: '/zlyyh-admin/order/syncOrderData',
    method: 'post',
    params: query
  })
}
