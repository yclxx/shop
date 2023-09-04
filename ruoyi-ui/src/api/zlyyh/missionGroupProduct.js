import request from '@/utils/request'

// 查询任务组可兑换商品配置列表
export function listMissionGroupProduct(query) {
  return request({
    url: '/zlyyh-admin/missionGroupProduct/list',
    method: 'get',
    params: query
  })
}

// 查询任务组可兑换商品配置详细
export function getMissionGroupProduct(id) {
  return request({
    url: '/zlyyh-admin/missionGroupProduct/' + id,
    method: 'get'
  })
}

// 新增任务组可兑换商品配置
export function addMissionGroupProduct(data) {
  return request({
    url: '/zlyyh-admin/missionGroupProduct',
    method: 'post',
    data: data
  })
}

// 修改任务组可兑换商品配置
export function updateMissionGroupProduct(data) {
  return request({
    url: '/zlyyh-admin/missionGroupProduct',
    method: 'put',
    data: data
  })
}

// 删除任务组可兑换商品配置
export function delMissionGroupProduct(id) {
  return request({
    url: '/zlyyh-admin/missionGroupProduct/' + id,
    method: 'delete'
  })
}