import request from '@/utils/request'

// 查询云闪付参数配置列表
export function listYsfConfig(query) {
  return request({
    url: '/zlyyh-admin/ysfConfig/list',
    method: 'get',
    params: query
  })
}

// 查询云闪付参数配置详细
export function getYsfConfig(configId) {
  return request({
    url: '/zlyyh-admin/ysfConfig/' + configId,
    method: 'get'
  })
}

// 新增云闪付参数配置
export function addYsfConfig(data) {
  return request({
    url: '/zlyyh-admin/ysfConfig',
    method: 'post',
    data: data
  })
}

// 修改云闪付参数配置
export function updateYsfConfig(data) {
  return request({
    url: '/zlyyh-admin/ysfConfig',
    method: 'put',
    data: data
  })
}

// 删除云闪付参数配置
export function delYsfConfig(configId) {
  return request({
    url: '/zlyyh-admin/ysfConfig/' + configId,
    method: 'delete'
  })
}
