import request from '@/utils/request'

// 查询第三方平台信息配置列表
export function listThirdPlatform(query) {
  return request({
    url: '/zlyyh/thirdPlatform/list',
    method: 'get',
    params: query
  })
}

// 查询第三方平台信息配置详细
export function getThirdPlatform(id) {
  return request({
    url: '/zlyyh/thirdPlatform/' + id,
    method: 'get'
  })
}

// 新增第三方平台信息配置
export function addThirdPlatform(data) {
  return request({
    url: '/zlyyh/thirdPlatform',
    method: 'post',
    data: data
  })
}

// 修改第三方平台信息配置
export function updateThirdPlatform(data) {
  return request({
    url: '/zlyyh/thirdPlatform',
    method: 'put',
    data: data
  })
}

// 删除第三方平台信息配置
export function delThirdPlatform(id) {
  return request({
    url: '/zlyyh/thirdPlatform/' + id,
    method: 'delete'
  })
}
