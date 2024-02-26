import request from '@/utils/request'

// 查询平台城市企业微信群列表
export function listPlatformCityGroup(query) {
  return request({
    url: '/zlyyh/platformCityGroup/list',
    method: 'get',
    params: query
  })
}

// 查询平台城市企业微信群详细
export function getPlatformCityGroup(id) {
  return request({
    url: '/zlyyh/platformCityGroup/' + id,
    method: 'get'
  })
}

// 新增平台城市企业微信群
export function addPlatformCityGroup(data) {
  return request({
    url: '/zlyyh/platformCityGroup',
    method: 'post',
    data: data
  })
}

// 修改平台城市企业微信群
export function updatePlatformCityGroup(data) {
  return request({
    url: '/zlyyh/platformCityGroup',
    method: 'put',
    data: data
  })
}

// 删除平台城市企业微信群
export function delPlatformCityGroup(id) {
  return request({
    url: '/zlyyh/platformCityGroup/' + id,
    method: 'delete'
  })
}
