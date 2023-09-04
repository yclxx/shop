import request from '@/utils/request'

// 查询门店组门店关联列表
export function listShopGroupRelevance(query) {
  return request({
    url: '/zlyyh-admin/shopGroupRelevance/list',
    method: 'get',
    params: query
  })
}

// 查询门店组门店关联详细
export function getShopGroupRelevance(id) {
  return request({
    url: '/zlyyh-admin/shopGroupRelevance/' + id,
    method: 'get'
  })
}

// 新增门店组门店关联
export function addShopGroupRelevance(data) {
  return request({
    url: '/zlyyh-admin/shopGroupRelevance',
    method: 'post',
    data: data
  })
}

// 修改门店组门店关联
export function updateShopGroupRelevance(data) {
  return request({
    url: '/zlyyh-admin/shopGroupRelevance',
    method: 'put',
    data: data
  })
}

// 删除门店组门店关联
export function delShopGroupRelevance(id) {
  return request({
    url: '/zlyyh-admin/shopGroupRelevance/' + id,
    method: 'delete'
  })
}
