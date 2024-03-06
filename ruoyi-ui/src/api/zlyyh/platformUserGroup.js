import request from '@/utils/request'

// 查询平台城市企业微信用户来源列表
export function listPlatformUserGroup(query) {
  return request({
    url: '/zlyyh/platformUserGroup/list',
    method: 'get',
    params: query
  })
}

// 查询平台城市企业微信用户来源详细
export function getPlatformUserGroup(id) {
  return request({
    url: '/zlyyh/platformUserGroup/' + id,
    method: 'get'
  })
}

// 新增平台城市企业微信用户来源
export function addPlatformUserGroup(data) {
  return request({
    url: '/zlyyh/platformUserGroup',
    method: 'post',
    data: data
  })
}

// 修改平台城市企业微信用户来源
export function updatePlatformUserGroup(data) {
  return request({
    url: '/zlyyh/platformUserGroup',
    method: 'put',
    data: data
  })
}

// 删除平台城市企业微信用户来源
export function delPlatformUserGroup(id) {
  return request({
    url: '/zlyyh/platformUserGroup/' + id,
    method: 'delete'
  })
}
