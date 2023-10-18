import request from '@/utils/request'

// 查询奖励发放记录列表
export function listMarketLog(query) {
  return request({
    url: '/zlyyh-admin/marketLog/list',
    method: 'get',
    params: query
  })
}

// 查询奖励发放记录详细
export function getMarketLog(logId) {
  return request({
    url: '/zlyyh-admin/marketLog/' + logId,
    method: 'get'
  })
}
