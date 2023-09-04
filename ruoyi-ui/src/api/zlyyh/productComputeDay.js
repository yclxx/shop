import request from '@/utils/request'

// 查询订单数据统计（每天）列表
export function listProductComputeDay(query) {
  return request({
    url: '/zlyyh-admin/productComputeDay/list',
    method: 'get',
    params: query
  })
}

// 查询订单数据统计（每天）详细
export function getProductComputeDay(id) {
  return request({
    url: '/zlyyh-admin/productComputeDay/' + id,
    method: 'get'
  })
}
