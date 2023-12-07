import request from '@/utils/request'

// 查询分销用户账户列表
export function listShareUserAccount(query) {
  return request({
    url: '/zlyyh-admin/shareUserAccount/list',
    method: 'get',
    params: query
  })
}

// 查询分销用户账户详细
export function getShareUserAccount(userId) {
  return request({
    url: '/zlyyh-admin/shareUserAccount/' + userId,
    method: 'get'
  })
}

// 新增分销用户账户
export function addShareUserAccount(data) {
  return request({
    url: '/zlyyh-admin/shareUserAccount',
    method: 'post',
    data: data
  })
}

// 修改分销用户账户
export function updateShareUserAccount(data) {
  return request({
    url: '/zlyyh-admin/shareUserAccount',
    method: 'put',
    data: data
  })
}

// 删除分销用户账户
export function delShareUserAccount(userId) {
  return request({
    url: '/zlyyh-admin/shareUserAccount/' + userId,
    method: 'delete'
  })
}