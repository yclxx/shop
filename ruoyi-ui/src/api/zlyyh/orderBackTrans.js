import request from '@/utils/request'

// 查询退款订单列表
export function listOrderBackTrans(query) {
  return request({
    url: '/zlyyh-admin/orderBackTrans/list',
    method: 'get',
    params: query
  })
}

// 查询退款订单详细
export function getOrderBackTrans(thNumber) {
  return request({
    url: '/zlyyh-admin/orderBackTrans/' + thNumber,
    method: 'get'
  })
}

// 新增退款订单
export function addOrderBackTrans(data) {
  return request({
    url: '/zlyyh-admin/orderBackTrans',
    method: 'post',
    data: data
  })
}

// 新增退款订单
export function addOrderBackTransDirect(data) {
  return request({
    url: '/zlyyh-admin/orderBackTrans/insertDirectByBo',
    method: 'post',
    data: data
  })
}

// 新增退款订单
export function addOrderBackTransHistory(data) {
  return request({
    url: '/zlyyh-admin/orderBackTrans/insertByBoHistory',
    method: 'post',
    data: data
  })
}

// 新增退款订单
export function addOrderBackTransDirectHistory(data) {
  return request({
    url: '/zlyyh-admin/orderBackTrans/insertDirectByBoHistory',
    method: 'post',
    data: data
  })
}

// 修改退款订单
export function updateOrderBackTrans(data) {
  return request({
    url: '/zlyyh-admin/orderBackTrans',
    method: 'put',
    data: data
  })
}

// 删除退款订单
export function delOrderBackTrans(thNumber) {
  return request({
    url: '/zlyyh-admin/orderBackTrans/' + thNumber,
    method: 'delete'
  })
}

// 订单退券
export function couponRefundOrder(thNumber) {
  return request({
    url: '/zlyyh-admin/order/couponRefundOrder/' + thNumber,
    method: 'get'
  })
}
