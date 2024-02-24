import request from '@/utils/request'

// 查询巡检奖励列表
export function listShopTourReward(query) {
  return request({
    url: '/zlyyh-admin/shopTourReward/list',
    method: 'get',
    params: query
  })
}

// 查询巡检奖励详细
export function getShopTourReward(tourRewardId) {
  return request({
    url: '/zlyyh-admin/shopTourReward/' + tourRewardId,
    method: 'get'
  })
}

// 新增巡检奖励
export function addShopTourReward(data) {
  return request({
    url: '/zlyyh-admin/shopTourReward',
    method: 'post',
    data: data
  })
}

// 修改巡检奖励
export function updateShopTourReward(data) {
  return request({
    url: '/zlyyh-admin/shopTourReward',
    method: 'put',
    data: data
  })
}

// 删除巡检奖励
export function delShopTourReward(tourRewardId) {
  return request({
    url: '/zlyyh-admin/shopTourReward/' + tourRewardId,
    method: 'delete'
  })
}