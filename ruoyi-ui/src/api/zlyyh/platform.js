import request from '@/utils/request'

// 查询平台信息列表
export function listPlatform(query) {
  return request({
    url: '/zlyyh-admin/platform/list',
    method: 'get',
    params: query
  })
}

// 查询平台下拉信息列表
export function selectListPlatform(query) {
  return request({
    url: '/zlyyh-admin/platform/selectList',
    method: 'get',
    params: query
  })
}

// 查询平台信息详细
export function getPlatform(platformKey) {
  return request({
    url: '/zlyyh-admin/platform/' + platformKey,
    method: 'get'
  })
}

// 新增平台信息
export function addPlatform(data) {
  return request({
    url: '/zlyyh-admin/platform',
    method: 'post',
    data: data
  })
}

// 修改平台信息
export function updatePlatform(data) {
  return request({
    url: '/zlyyh-admin/platform',
    method: 'put',
    data: data
  })
}

// 删除平台信息
export function delPlatform(platformKey) {
  return request({
    url: '/zlyyh-admin/platform/' + platformKey,
    method: 'delete'
  })
}
