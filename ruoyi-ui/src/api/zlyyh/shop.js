import request from '@/utils/request'

// 查询门店列表
export function listShop(query) {
  return request({
    url: '/zlyyh-admin/shop/list',
    method: 'get',
    params: query
  })
}
// 特殊查询条件
export function listShops(query) {
  return request({
    url: '/zlyyh-admin/shop/lists',
    method: 'get',
    params: query
  })
}

// 查询门店详细
export function getShop(shopId) {
  return request({
    url: '/zlyyh-admin/shop/' + shopId,
    method: 'get'
  })
}

// 新增门店
export function addShop(data) {
  return request({
    url: '/zlyyh-admin/shop',
    method: 'post',
    data: data
  })
}

// 修改门店
export function updateShop(data) {
  return request({
    url: '/zlyyh-admin/shop',
    method: 'put',
    data: data
  })
}

// 删除门店
export function delShop(shopId) {
  return request({
    url: '/zlyyh-admin/shop/' + shopId,
    method: 'delete'
  })
}

// 查询门店下拉信息列表
export function selectShopList(query) {
  return request({
    url: '/zlyyh-admin/shop/selectShopList',
    method: 'get',
    params: query
  })
}

// 查询门店下拉信息列表
export function selectShopListById(query) {
  return request({
    url: '/zlyyh-admin/shop/selectShopListById',
    method: 'get',
    params: query
  })
}
