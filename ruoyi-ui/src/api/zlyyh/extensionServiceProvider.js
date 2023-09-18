import request from '@/utils/request'

// 查询商户拓展服务商列表
export function listExtensionServiceProvider(query) {
  return request({
    url: '/zlyyh-admin/extensionServiceProvider/list',
    method: 'get',
    params: query
  })
}

// 查询商户拓展服务商详细
export function getExtensionServiceProvider(id) {
  return request({
    url: '/zlyyh-admin/extensionServiceProvider/' + id,
    method: 'get'
  })
}

// 新增商户拓展服务商
export function addExtensionServiceProvider(data) {
  return request({
    url: '/zlyyh-admin/extensionServiceProvider',
    method: 'post',
    data: data
  })
}

// 修改商户拓展服务商
export function updateExtensionServiceProvider(data) {
  return request({
    url: '/zlyyh-admin/extensionServiceProvider',
    method: 'put',
    data: data
  })
}

// 删除商户拓展服务商
export function delExtensionServiceProvider(id) {
  return request({
    url: '/zlyyh-admin/extensionServiceProvider/' + id,
    method: 'delete'
  })
}
