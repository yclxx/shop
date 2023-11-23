import request from '@/utils/request'

// 查询推广任务列表
export function listPromotionTask(query) {
  return request({
    url: '/zlyyh-admin/promotionTask/list',
    method: 'get',
    params: query
  })
}

// 查询推广任务详细
export function getPromotionTask(taskId) {
  return request({
    url: '/zlyyh-admin/promotionTask/' + taskId,
    method: 'get'
  })
}

// 新增推广任务
export function addPromotionTask(data) {
  return request({
    url: '/zlyyh-admin/promotionTask',
    method: 'post',
    data: data
  })
}

// 修改推广任务
export function updatePromotionTask(data) {
  return request({
    url: '/zlyyh-admin/promotionTask',
    method: 'put',
    data: data
  })
}

// 删除推广任务
export function delPromotionTask(taskId) {
  return request({
    url: '/zlyyh-admin/promotionTask/' + taskId,
    method: 'delete'
  })
}
