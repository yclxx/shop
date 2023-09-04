import request from '@/utils/request'

// 查询订单数据统计（月份）列表
export function listProductComputeMonth(query) {
  return request({
    url: '/zlyyh-admin/productComputeMonth/list',
    method: 'get',
    params: query
  })
}

// 查询订单数据统计（月份）详细
export function getProductComputeMonth(id) {
  return request({
    url: '/zlyyh-admin/productComputeMonth/' + id,
    method: 'get'
  })
}
