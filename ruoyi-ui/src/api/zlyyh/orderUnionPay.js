import request from '@/utils/request'

// 查询银联分销订单详情列表
export function listOrderUnionPay(query) {
  return request({
    url: '/zlyyh-admin/orderUnionPay/list',
    method: 'get',
    params: query
  })
}

// 查询银联分销订单详情详细
export function getOrderUnionPay(number) {
  return request({
    url: '/zlyyh-admin/orderUnionPay/' + number,
    method: 'get'
  })
}

// 新增银联分销订单详情
export function addOrderUnionPay(data) {
  return request({
    url: '/zlyyh-admin/orderUnionPay',
    method: 'post',
    data: data
  })
}

// 修改银联分销订单详情
export function updateOrderUnionPay(data) {
  return request({
    url: '/zlyyh-admin/orderUnionPay',
    method: 'put',
    data: data
  })
}

// 删除银联分销订单详情
export function delOrderUnionPay(number) {
  return request({
    url: '/zlyyh-admin/orderUnionPay/' + number,
    method: 'delete'
  })
}
