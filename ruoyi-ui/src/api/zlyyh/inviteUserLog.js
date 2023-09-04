import request from '@/utils/request'

// 查询邀请记录列表
export function listInviteUserLog(query) {
  return request({
    url: '/zlyyh-admin/inviteUserLog/list',
    method: 'get',
    params: query
  })
}

// 查询邀请记录详细
export function getInviteUserLog(id) {
  return request({
    url: '/zlyyh-admin/inviteUserLog/' + id,
    method: 'get'
  })
}

// 新增邀请记录
export function addInviteUserLog(data) {
  return request({
    url: '/zlyyh-admin/inviteUserLog',
    method: 'post',
    data: data
  })
}

// 修改邀请记录
export function updateInviteUserLog(data) {
  return request({
    url: '/zlyyh-admin/inviteUserLog',
    method: 'put',
    data: data
  })
}

// 删除邀请记录
export function delInviteUserLog(id) {
  return request({
    url: '/zlyyh-admin/inviteUserLog/' + id,
    method: 'delete'
  })
}