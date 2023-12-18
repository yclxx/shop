import request from '@/utils/request'

// 查询用户订阅列表
export function listSendDyInfo(query) {
  return request({
    url: '/zlyyh-admin/sendDyInfo/list',
    method: 'get',
    params: query
  })
}

// 查询用户订阅详细
export function getSendDyInfo(id) {
  return request({
    url: '/zlyyh-admin/sendDyInfo/' + id,
    method: 'get'
  })
}

// 新增用户订阅
export function addSendDyInfo(data) {
  return request({
    url: '/zlyyh-admin/sendDyInfo',
    method: 'post',
    data: data
  })
}

// 修改用户订阅
export function updateSendDyInfo(data) {
  return request({
    url: '/zlyyh-admin/sendDyInfo',
    method: 'put',
    data: data
  })
}

// 删除用户订阅
export function delSendDyInfo(id) {
  return request({
    url: '/zlyyh-admin/sendDyInfo/' + id,
    method: 'delete'
  })
}
