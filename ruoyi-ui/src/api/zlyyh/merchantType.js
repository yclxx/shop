import request from '@/utils/request'

// 查询商户门店类别列表
export function listMerchantType(query) {
  return request({
    url: '/zlyyh-admin/merchantType/list',
    method: 'get',
    params: query
  })
}

// 查询商户门店类别详细
export function getMerchantType(merchantTypeId) {
  return request({
    url: '/zlyyh-admin/merchantType/' + merchantTypeId,
    method: 'get'
  })
}

// 新增商户门店类别
export function addMerchantType(data) {
  return request({
    url: '/zlyyh-admin/merchantType',
    method: 'post',
    data: data
  })
}

// 修改商户门店类别
export function updateMerchantType(data) {
  return request({
    url: '/zlyyh-admin/merchantType',
    method: 'put',
    data: data
  })
}

// 删除商户门店类别
export function delMerchantType(merchantTypeId) {
  return request({
    url: '/zlyyh-admin/merchantType/' + merchantTypeId,
    method: 'delete'
  })
}

// 查询类别下拉信息列表
export function selectMerTypeList(query) {
  return request({
    url: '/zlyyh-admin/merchantType/selectMerTypeList',
    method: 'get',
    params: query
  })
}