import request from '@/utils/request'

// 查询商户号列表
export function listMerchant(query) {
  return request({
    url: '/zlyyh-admin/merchant/list',
    method: 'get',
    params: query
  })
}

// 查询商户号下拉列表
export function listSelectMerchant(query) {
  return request({
    url: '/zlyyh-admin/merchant/selectList',
    method: 'get',
    params: query
  })
}

// 查询商户号详细
export function getMerchant(id) {
  return request({
    url: '/zlyyh-admin/merchant/' + id,
    method: 'get'
  })
}

// 新增商户号
export function addMerchant(data) {
  return request({
    url: '/zlyyh-admin/merchant',
    method: 'post',
    data: data
  })
}

// 修改商户号
export function updateMerchant(data) {
  return request({
    url: '/zlyyh-admin/merchant',
    method: 'put',
    data: data
  })
}

// 删除商户号
export function delMerchant(id) {
  return request({
    url: '/zlyyh-admin/merchant/' + id,
    method: 'delete'
  })
}
