import request from '@/utils/request'

// 查询商品列表
export function listProduct(query) {
  return request({
    url: '/zlyyh-admin/product/list',
    method: 'get',
    params: query
  })
}

// 查询商品列表
export function categoryProductList(query) {
  return request({
    url: '/zlyyh-admin/product/categoryProductList',
    method: 'get',
    params: query
  })
}

// 查询商品列表
export function categoryPlatformProductList(query) {
  return request({
    url: '/zlyyh-admin/product/categoryPlatformProductList',
    method: 'get',
    params: query
  })
}

// 查询商品列表
export function groupProductList(query) {
  return request({
    url: '/zlyyh-admin/product/groupProductList',
    method: 'get',
    params: query
  })
}
// 商品关联查询列表
export function joinListProduct(query) {
  return request({
    url: '/zlyyh-admin/product/joinListProduct',
    method: 'get',
    params: query
  })
}

// 查询商品详细
export function getProduct(productId) {
  return request({
    url: '/zlyyh-admin/product/' + productId,
    method: 'get'
  })
}

// 新增商品
export function addProduct(data) {
  return request({
    url: '/zlyyh-admin/product',
    method: 'post',
    data: data
  })
}

// 修改商品
export function updateProduct(data) {
  return request({
    url: '/zlyyh-admin/product',
    method: 'put',
    data: data
  })
}

// 删除商品
export function delProduct(productId) {
  return request({
    url: '/zlyyh-admin/product/' + productId,
    method: 'delete'
  })
}

// 设置商品抢购状态
export function setProductDayCount(dayCount, data) {
  return request({
    url: '/zlyyh-admin/product/setProductDayCount/' + dayCount,
    method: 'put',
    data: data
  })
}

// 查询平台下拉信息列表
export function selectListProduct(query) {
  return request({
    url: '/zlyyh-admin/product/selectListProduct',
    method: 'get',
    params: query
  })
}
