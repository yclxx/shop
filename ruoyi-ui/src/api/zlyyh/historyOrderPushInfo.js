import request from '@/utils/request'

// 查询历史订单取码记录列表
export function listHistoryOrderPushInfo(query) {
  return request({
    url: '/zlyyh-admin/historyOrderPushInfo/list',
    method: 'get',
    params: query
  })
}

// 查询历史订单取码记录详细
export function getHistoryOrderPushInfo(id) {
  return request({
    url: '/zlyyh-admin/historyOrderPushInfo/' + id,
    method: 'get'
  })
}

// 新增历史订单取码记录
export function addHistoryOrderPushInfo(data) {
  return request({
    url: '/zlyyh-admin/historyOrderPushInfo',
    method: 'post',
    data: data
  })
}

// 修改历史订单取码记录
export function updateHistoryOrderPushInfo(data) {
  return request({
    url: '/zlyyh-admin/historyOrderPushInfo',
    method: 'put',
    data: data
  })
}

// 删除历史订单取码记录
export function delHistoryOrderPushInfo(id) {
  return request({
    url: '/zlyyh-admin/historyOrderPushInfo/' + id,
    method: 'delete'
  })
}
