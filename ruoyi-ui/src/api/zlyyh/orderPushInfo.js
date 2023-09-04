import request from '@/utils/request'

// 查询订单取码记录列表
export function listOrderPushInfo(query) {
  return request({
    url: '/zlyyh-admin/orderPushInfo/list',
    method: 'get',
    params: query
  })
}

// 查询订单取码记录详细
export function getOrderPushInfo(id) {
  return request({
    url: '/zlyyh-admin/orderPushInfo/' + id,
    method: 'get'
  })
}

// 新增订单取码记录
export function addOrderPushInfo(data) {
  return request({
    url: '/zlyyh-admin/orderPushInfo',
    method: 'post',
    data: data
  })
}

// 修改订单取码记录
export function updateOrderPushInfo(data) {
  return request({
    url: '/zlyyh-admin/orderPushInfo',
    method: 'put',
    data: data
  })
}

// 删除订单取码记录
export function delOrderPushInfo(id) {
  return request({
    url: '/zlyyh-admin/orderPushInfo/' + id,
    method: 'delete'
  })
}
