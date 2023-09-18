import request from '@/utils/request'

// 查询观影用户信息列表
export function listUserIdcard(query) {
  return request({
    url: '/zlyyh-admin/userIdcard/list',
    method: 'get',
    params: query
  })
}

// 查询观影用户信息详细
export function getUserIdcard(userIdcardId) {
  return request({
    url: '/zlyyh-admin/userIdcard/' + userIdcardId,
    method: 'get'
  })
}

// 新增观影用户信息
export function addUserIdcard(data) {
  return request({
    url: '/zlyyh-admin/userIdcard',
    method: 'post',
    data: data
  })
}

// 修改观影用户信息
export function updateUserIdcard(data) {
  return request({
    url: '/zlyyh-admin/userIdcard',
    method: 'put',
    data: data
  })
}

// 删除观影用户信息
export function delUserIdcard(userIdcardId) {
  return request({
    url: '/zlyyh-admin/userIdcard/' + userIdcardId,
    method: 'delete'
  })
}
