import request from '@/utils/request'

// 查询历史订单扩展信息列表
export function listHistoryOrderInfo(query) {
  return request({
    url: '/zlyyh-admin/historyOrderInfo/list',
    method: 'get',
    params: query
  })
}

// 查询历史订单扩展信息详细
export function getHistoryOrderInfo(number) {
  return request({
    url: '/zlyyh-admin/historyOrderInfo/' + number,
    method: 'get'
  })
}

// 新增历史订单扩展信息
export function addHistoryOrderInfo(data) {
  return request({
    url: '/zlyyh-admin/historyOrderInfo',
    method: 'post',
    data: data
  })
}

// 修改历史订单扩展信息
export function updateHistoryOrderInfo(data) {
  return request({
    url: '/zlyyh-admin/historyOrderInfo',
    method: 'put',
    data: data
  })
}

// 删除历史订单扩展信息
export function delHistoryOrderInfo(number) {
  return request({
    url: '/zlyyh-admin/historyOrderInfo/' + number,
    method: 'delete'
  })
}
