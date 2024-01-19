import request from '@/utils/request'

// 查询商品组规则配置列表
export function listProductGroup(query) {
  return request({
    url: '/zlyyh-admin/productGroup/list',
    method: 'get',
    params: query
  })
}

// 查询商品组规则配置详细
export function getProductGroup(productGroupId) {
  return request({
    url: '/zlyyh-admin/productGroup/' + productGroupId,
    method: 'get'
  })
}

// 新增商品组规则配置
export function addProductGroup(data) {
  return request({
    url: '/zlyyh-admin/productGroup',
    method: 'post',
    data: data
  })
}

// 修改商品组规则配置
export function updateProductGroup(data) {
  return request({
    url: '/zlyyh-admin/productGroup',
    method: 'put',
    data: data
  })
}

// 删除商品组规则配置
export function delProductGroup(productGroupId) {
  return request({
    url: '/zlyyh-admin/productGroup/' + productGroupId,
    method: 'delete'
  })
}

// 新增优惠券批次
export function updateGroupProduct(data) {
  return request({
    url: '/zlyyh-admin/productGroup/updateGroupProduct',
    method: 'post',
    data: data
  })
}
