import request from '@/utils/request'

// 查询营销活动列表
export function listMarketActivity(query) {
  return request({
    url: '/zlyyh-admin/marketActivity/list',
    method: 'get',
    params: query
  })
}

// 查询营销活动详细
export function getMarketActivity(activityId) {
  return request({
    url: '/zlyyh-admin/marketActivity/' + activityId,
    method: 'get'
  })
}

// 新增营销活动
export function addMarketActivity(data) {
  return request({
    url: '/zlyyh-admin/marketActivity',
    method: 'post',
    data: data
  })
}

// 修改营销活动
export function updateMarketActivity(data) {
  return request({
    url: '/zlyyh-admin/marketActivity',
    method: 'put',
    data: data
  })
}

// 删除营销活动
export function delMarketActivity(activityId) {
  return request({
    url: '/zlyyh-admin/marketActivity/' + activityId,
    method: 'delete'
  })
}
