import request from '@/utils/request'

// 查询门店商户号列表
export function listShopMerchant(query) {
  return request({
    url: '/zlyyh-admin/shopMerchant/list',
    method: 'get',
    params: query
  })
}

// 查询门店商户号详细
export function getShopMerchant(id) {
  return request({
    url: '/zlyyh-admin/shopMerchant/' + id,
    method: 'get'
  })
}

// 新增门店商户号
export function addShopMerchant(data) {
  return request({
    url: '/zlyyh-admin/shopMerchant',
    method: 'post',
    data: data
  })
}

// 修改门店商户号
export function updateShopMerchant(data) {
  return request({
    url: '/zlyyh-admin/shopMerchant',
    method: 'put',
    data: data
  })
}

// 删除门店商户号
export function delShopMerchant(id) {
  return request({
    url: '/zlyyh-admin/shopMerchant/' + id,
    method: 'delete'
  })
}
