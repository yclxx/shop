import request from '@/utils/request'

// 查询任务用户列表
export function listMissionUser(query) {
  return request({
    url: '/zlyyh-admin/missionUser/list',
    method: 'get',
    params: query
  })
}

// 查询任务用户详细
export function getMissionUser(missionUserId) {
  return request({
    url: '/zlyyh-admin/missionUser/' + missionUserId,
    method: 'get'
  })
}

// 新增任务用户
export function addMissionUser(data) {
  return request({
    url: '/zlyyh-admin/missionUser',
    method: 'post',
    data: data
  })
}

// 修改任务用户
export function updateMissionUser(data) {
  return request({
    url: '/zlyyh-admin/missionUser',
    method: 'put',
    data: data
  })
}

// 删除任务用户
export function delMissionUser(missionUserId) {
  return request({
    url: '/zlyyh-admin/missionUser/' + missionUserId,
    method: 'delete'
  })
}