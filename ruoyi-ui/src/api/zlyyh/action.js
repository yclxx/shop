import request from '@/utils/request'

// 查询优惠券批次列表
export function listAction(query) {
  return request({
    url: '/zlyyh-admin/action/list',
    method: 'get',
    params: query
  })
}

// 查询优惠券批次详细
export function getAction(actionId) {
  return request({
    url: '/zlyyh-admin/action/' + actionId,
    method: 'get'
  })
}

// 新增优惠券批次
export function addAction(data) {
  return request({
    url: '/zlyyh-admin/action',
    method: 'post',
    data: data
  })
}
// 新增优惠券批次
export function updateActionProduct(data) {
  return request({
    url: '/zlyyh-admin/action/updateActionProduct',
    method: 'post',
    data: data
  })
}

// 批量生成优惠券
export function createCoupon(data) {
  return request({
    url: '/zlyyh-admin/action/createCoupon',
    method: 'post',
    data: data
  })
}

// 修改优惠券批次
export function updateAction(data) {
  return request({
    url: '/zlyyh-admin/action',
    method: 'put',
    data: data
  })
}

// 删除优惠券批次
export function delAction(actionId) {
  return request({
    url: '/zlyyh-admin/action/' + actionId,
    method: 'delete'
  })
}
