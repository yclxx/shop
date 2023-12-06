import request from '@/utils/request'

// 查询推广任务记录列表
export function listPromotionLog(query) {
  return request({
    url: '/zlyyh-admin/promotionLog/list',
    method: 'get',
    params: query
  })
}

// 查询推广任务记录详细
export function getPromotionLog(id) {
  return request({
    url: '/zlyyh-admin/promotionLog/' + id,
    method: 'get'
  })
}
