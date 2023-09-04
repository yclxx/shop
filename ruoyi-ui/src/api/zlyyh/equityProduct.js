import request from '@/utils/request'

// 查询权益包商品列表
export function listEquityProduct(query) {
  return request({
    url: '/zlyyh-admin/equityProduct/list',
    method: 'get',
    params: query
  })
}

// 查询权益包商品详细
export function getEquityProduct(id) {
  return request({
    url: '/zlyyh-admin/equityProduct/' + id,
    method: 'get'
  })
}

// 新增权益包商品
export function addEquityProduct(data) {
  return request({
    url: '/zlyyh-admin/equityProduct',
    method: 'post',
    data: data
  })
}

// 修改权益包商品
export function updateEquityProduct(data) {
  return request({
    url: '/zlyyh-admin/equityProduct',
    method: 'put',
    data: data
  })
}

// 删除权益包商品
export function delEquityProduct(id) {
  return request({
    url: '/zlyyh-admin/equityProduct/' + id,
    method: 'delete'
  })
}