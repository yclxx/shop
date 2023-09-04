import request from '@/utils/request'

// 查询门店组配置列表
export function listShopGroup(query) {
  return request({
    url: '/zlyyh-admin/shopGroup/list',
    method: 'get',
    params: query
  })
}

// 查询门店组配置下拉列表
export function selectListShopGroup(query) {
  return request({
    url: '/zlyyh-admin/shopGroup/selectList',
    method: 'get',
    params: query
  })
}

// 查询门店组配置详细
export function getShopGroup(shopGroupId) {
  return request({
    url: '/zlyyh-admin/shopGroup/' + shopGroupId,
    method: 'get'
  })
}

// 新增门店组配置
export function addShopGroup(data) {
  return request({
    url: '/zlyyh-admin/shopGroup',
    method: 'post',
    data: data
  })
}

// 修改门店组配置
export function updateShopGroup(data) {
  return request({
    url: '/zlyyh-admin/shopGroup',
    method: 'put',
    data: data
  })
}

// 删除门店组配置
export function delShopGroup(shopGroupId) {
  return request({
    url: '/zlyyh-admin/shopGroup/' + shopGroupId,
    method: 'delete'
  })
}
