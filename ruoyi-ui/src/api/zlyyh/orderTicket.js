import request from '@/utils/request'

// 查询演出票订单列表
export function listOrderTicket(query) {
  return request({
    url: '/zlyyh-admin/orderTicket/list',
    method: 'get',
    params: query
  })
}

// 查询演出票订单详细
export function getOrderTicket(number) {
  return request({
    url: '/zlyyh-admin/orderTicket/' + number,
    method: 'get'
  })
}

// 新增演出票订单
export function addOrderTicket(data) {
  return request({
    url: '/zlyyh-admin/orderTicket',
    method: 'post',
    data: data
  })
}

// 修改演出票订单
export function updateOrderTicket(data) {
  return request({
    url: '/zlyyh-admin/orderTicket',
    method: 'put',
    data: data
  })
}

// 删除演出票订单
export function delOrderTicket(number) {
  return request({
    url: '/zlyyh-admin/orderTicket/' + number,
    method: 'delete'
  })
}

// 核销
export function writeOffCode(codeNo) {
  return request({
    url: '/zlyyh-admin/orderTicket/writeOffCode/' + codeNo,
    method: 'get'
  })
}

// 票券返还
export function voidCode(codeNo) {
  return request({
    url: '/zlyyh-admin/orderTicket/voidCode/' + codeNo,
    method: 'get'
  })
}

// 作废
export function returnCode(codeNo) {
  return request({
    url: '/zlyyh-admin/orderTicket/returnCode/' + codeNo,
    method: 'get'
  })
}
