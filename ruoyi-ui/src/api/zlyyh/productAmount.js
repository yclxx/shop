import request from '@/utils/request'

// 查询商品价格配置列表
export function listProductAmount(query) {
  return request({
    url: '/zlyyh-admin/productAmount/list',
    method: 'get',
    params: query
  })
}

// 查询商品价格配置详细
export function getProductAmount(amountId) {
  return request({
    url: '/zlyyh-admin/productAmount/' + amountId,
    method: 'get'
  })
}

// 新增商品价格配置
export function addProductAmount(data) {
  return request({
    url: '/zlyyh-admin/productAmount',
    method: 'post',
    data: data
  })
}

// 修改商品价格配置
export function updateProductAmount(data) {
  return request({
    url: '/zlyyh-admin/productAmount',
    method: 'put',
    data: data
  })
}

// 删除商品价格配置
export function delProductAmount(amountId) {
  return request({
    url: '/zlyyh-admin/productAmount/' + amountId,
    method: 'delete'
  })
}
