import request from '@/utils/request'

// 查询银联任务用户列表
export function listUnionpayMissionUser(query) {
  return request({
    url: '/zlyyh-admin/unionpayMissionUser/list',
    method: 'get',
    params: query
  })
}

// 查询银联任务用户详细
export function getUnionpayMissionUser(upMissionUserId) {
  return request({
    url: '/zlyyh-admin/unionpayMissionUser/' + upMissionUserId,
    method: 'get'
  })
}

// 新增银联任务用户
export function addUnionpayMissionUser(data) {
  return request({
    url: '/zlyyh-admin/unionpayMissionUser',
    method: 'post',
    data: data
  })
}

// 修改银联任务用户
export function updateUnionpayMissionUser(data) {
  return request({
    url: '/zlyyh-admin/unionpayMissionUser',
    method: 'put',
    data: data
  })
}

// 删除银联任务用户
export function delUnionpayMissionUser(upMissionUserId) {
  return request({
    url: '/zlyyh-admin/unionpayMissionUser/' + upMissionUserId,
    method: 'delete'
  })
}