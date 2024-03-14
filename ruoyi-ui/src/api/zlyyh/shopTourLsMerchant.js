import request from '@/utils/request'

// 查询巡检商户号临时列表
export function listShopTourLsMerchant(query) {
  return request({
    url: '/zlyyh-admin/shopTourLsMerchant/list',
    method: 'get',
    params: query
  })
}

// 查询巡检商户号临时详细
export function getShopTourLsMerchant(tourMerchantLsId) {
  return request({
    url: '/zlyyh-admin/shopTourLsMerchant/' + tourMerchantLsId,
    method: 'get'
  })
}

// 新增巡检商户号临时
export function addShopTourLsMerchant(data) {
  return request({
    url: '/zlyyh-admin/shopTourLsMerchant',
    method: 'post',
    data: data
  })
}

// 修改巡检商户号临时
export function updateShopTourLsMerchant(data) {
  return request({
    url: '/zlyyh-admin/shopTourLsMerchant',
    method: 'put',
    data: data
  })
}

// 删除巡检商户号临时
export function delShopTourLsMerchant(tourMerchantLsId) {
  return request({
    url: '/zlyyh-admin/shopTourLsMerchant/' + tourMerchantLsId,
    method: 'delete'
  })
}