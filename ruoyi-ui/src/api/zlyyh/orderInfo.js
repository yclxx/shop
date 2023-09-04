import request from '@/utils/request'

// 查询订单扩展信息列表
export function listOrderInfo(query) {
  return request({
    url: '/zlyyh-admin/orderInfo/list',
    method: 'get',
    params: query
  })
}

// 查询订单扩展信息详细
export function getOrderInfo(number) {
  return request({
    url: '/zlyyh-admin/orderInfo/' + number,
    method: 'get'
  })
}

// 新增订单扩展信息
export function addOrderInfo(data) {
  return request({
    url: '/zlyyh-admin/orderInfo',
    method: 'post',
    data: data
  })
}

// 修改订单扩展信息
export function updateOrderInfo(data) {
  return request({
    url: '/zlyyh-admin/orderInfo',
    method: 'put',
    data: data
  })
}

// 删除订单扩展信息
export function delOrderInfo(number) {
  return request({
    url: '/zlyyh-admin/orderInfo/' + number,
    method: 'delete'
  })
}
