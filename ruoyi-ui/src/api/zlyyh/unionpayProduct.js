import request from '@/utils/request'

// 查询银联活动列表
export function listUnionpayProduct(query) {
  return request({
    url: '/zlyyh-admin/unionpayProduct/list',
    method: 'get',
    params: query
  })
}

// 查询银联活动详细
export function getUnionpayProduct(activityNo) {
  return request({
    url: '/zlyyh-admin/unionpayProduct/' + activityNo,
    method: 'get'
  })
}

// 新增银联活动
export function addUnionpayProduct(data) {
  return request({
    url: '/zlyyh-admin/unionpayProduct',
    method: 'post',
    data: data
  })
}

// 修改银联活动
export function updateUnionpayProduct(data) {
  return request({
    url: '/zlyyh-admin/unionpayProduct',
    method: 'put',
    data: data
  })
}

// 删除银联活动
export function delUnionpayProduct(activityNo) {
  return request({
    url: '/zlyyh-admin/unionpayProduct/' + activityNo,
    method: 'delete'
  })
}