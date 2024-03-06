import request from '@/utils/request'

// 查询巡检活动列表
export function listShopTourActivity(query) {
  return request({
    url: '/zlyyh-admin/shopTourActivity/list',
    method: 'get',
    params: query
  })
}

// 查询巡检活动详细
export function getShopTourActivity(tourActivityId) {
  return request({
    url: '/zlyyh-admin/shopTourActivity/' + tourActivityId,
    method: 'get'
  })
}

// 新增巡检活动
export function addShopTourActivity(data) {
  return request({
    url: '/zlyyh-admin/shopTourActivity',
    method: 'post',
    data: data
  })
}

// 修改巡检活动
export function updateShopTourActivity(data) {
  return request({
    url: '/zlyyh-admin/shopTourActivity',
    method: 'put',
    data: data
  })
}

// 删除巡检活动
export function delShopTourActivity(tourActivityId) {
  return request({
    url: '/zlyyh-admin/shopTourActivity/' + tourActivityId,
    method: 'delete'
  })
}

// 查询巡检活动下拉信息列表
export function selectListTourActivity(query) {
  return request({
    url: '/zlyyh-admin/shopTourActivity/selectListTourActivity',
    method: 'get',
    params: query
  })
}