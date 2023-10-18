import request from '@/utils/request'

export function listMarket(query) {
  return request({
    url: '/zlyyh-admin/market/list',
    method: 'get',
    params: query
  })
}

// 查询新用户营销详细
export function getMarket(marketId) {
  return request({
    url: '/zlyyh-admin/market/' + marketId,
    method: 'get'
  })
}

// 查询新用户营销详细
export function getMarketPrize(marketId) {
  return request({
    url: '/zlyyh-admin/market/prize/' + marketId,
    method: 'get'
  })
}

// 新增新用户营销
export function addMarket(data) {
  return request({
    url: '/zlyyh-admin/market',
    method: 'post',
    data: data
  })
}

// 修改新用户营销
export function updateMarket(data) {
  return request({
    url: '/zlyyh-admin/market',
    method: 'put',
    data: data
  })
}

// 删除新用户营销
export function delMarket(marketId) {
  return request({
    url: '/zlyyh-admin/market/' + marketId,
    method: 'delete'
  })
}
