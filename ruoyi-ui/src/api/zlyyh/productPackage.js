import request from '@/utils/request'

// 查询商品券包列表
export function listProductPackage(query) {
  return request({
    url: '/zlyyh-admin/productPackage/list',
    method: 'get',
    params: query
  })
}

// 查询商品券包详细
export function getProductPackage(packageId) {
  return request({
    url: '/zlyyh-admin/productPackage/' + packageId,
    method: 'get'
  })
}

// 新增商品券包
export function addProductPackage(data) {
  return request({
    url: '/zlyyh-admin/productPackage',
    method: 'post',
    data: data
  })
}

// 修改商品券包
export function updateProductPackage(data) {
  return request({
    url: '/zlyyh-admin/productPackage',
    method: 'put',
    data: data
  })
}

// 删除商品券包
export function delProductPackage(packageId) {
  return request({
    url: '/zlyyh-admin/productPackage/' + packageId,
    method: 'delete'
  })
}