import request from '@/utils/request'

// 查询订单下载记录列表
export function listOrderDownloadLog(query) {
  return request({
    url: '/zlyyh-admin/orderDownloadLog/list',
    method: 'get',
    params: query
  })
}

// 查询订单下载记录详细
export function getOrderDownloadLog(tOrderDownloadId) {
  return request({
    url: '/zlyyh-admin/orderDownloadLog/' + tOrderDownloadId,
    method: 'get'
  })
}

// 新增订单下载记录
export function addOrderDownloadLog(data) {
  return request({
    url: '/zlyyh-admin/orderDownloadLog',
    method: 'post',
    data: data
  })
}

// 修改订单下载记录
export function updateOrderDownloadLog(data) {
  return request({
    url: '/zlyyh-admin/orderDownloadLog',
    method: 'put',
    data: data
  })
}

// 删除订单下载记录
export function delOrderDownloadLog(tOrderDownloadId) {
  return request({
    url: '/zlyyh-admin/orderDownloadLog/' + tOrderDownloadId,
    method: 'delete'
  })
}
