import request from '@/utils/request'

// 查询自定义首页列表
export function listPlatformCityIndex(query) {
  return request({
    url: '/zlyyh-admin/platformCityIndex/list',
    method: 'get',
    params: query
  })
}

// 查询自定义首页详细
export function getPlatformCityIndex(id) {
  return request({
    url: '/zlyyh-admin/platformCityIndex/' + id,
    method: 'get'
  })
}

// 新增自定义首页
export function addPlatformCityIndex(data) {
  return request({
    url: '/zlyyh-admin/platformCityIndex',
    method: 'post',
    data: data
  })
}

// 修改自定义首页
export function updatePlatformCityIndex(data) {
  return request({
    url: '/zlyyh-admin/platformCityIndex',
    method: 'put',
    data: data
  })
}

// 删除自定义首页
export function delPlatformCityIndex(id) {
  return request({
    url: '/zlyyh-admin/platformCityIndex/' + id,
    method: 'delete'
  })
}