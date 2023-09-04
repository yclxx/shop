import request from '@/utils/request'

// 查询历史订单列表
export function listHistoryOrder(query) {
  return request({
    url: '/zlyyh-admin/historyOrder/list',
    method: 'get',
    params: query
  })
}

// 查询历史订单详细
export function getHistoryOrder(number) {
  return request({
    url: '/zlyyh-admin/historyOrder/' + number,
    method: 'get'
  })
}

// 新增历史订单
export function addHistoryOrder(data) {
  return request({
    url: '/zlyyh-admin/historyOrder',
    method: 'post',
    data: data
  })
}

// 修改历史订单
export function updateHistoryOrder(data) {
  return request({
    url: '/zlyyh-admin/historyOrder',
    method: 'put',
    data: data
  })
}

// 美食退款
export function cancelFoodOrder(number) {
  return request({
    url: '/zlyyh-admin/historyOrder/cancelFoodOrder/' + number,
    method: 'get'
  })
}

// 删除历史订单
export function delHistoryOrder(number) {
  return request({
    url: '/zlyyh-admin/historyOrder/' + number,
    method: 'delete'
  })
}
