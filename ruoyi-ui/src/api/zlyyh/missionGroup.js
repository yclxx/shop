import request from '@/utils/request'

// 查询任务组列表
export function listMissionGroup(query) {
  return request({
    url: '/zlyyh-admin/missionGroup/list',
    method: 'get',
    params: query
  })
}

// 查询任务组列表
export function listMissionGroupSelect(query) {
  return request({
    url: '/zlyyh-admin/missionGroup/selectList',
    method: 'get',
    params: query
  })
}

// 查询任务组详细
export function getMissionGroup(missionGroupId) {
  return request({
    url: '/zlyyh-admin/missionGroup/' + missionGroupId,
    method: 'get'
  })
}

// 新增任务组
export function addMissionGroup(data) {
  return request({
    url: '/zlyyh-admin/missionGroup',
    method: 'post',
    data: data
  })
}

// 修改任务组
export function updateMissionGroup(data) {
  return request({
    url: '/zlyyh-admin/missionGroup',
    method: 'put',
    data: data
  })
}

// 删除任务组
export function delMissionGroup(missionGroupId) {
  return request({
    url: '/zlyyh-admin/missionGroup/' + missionGroupId,
    method: 'delete'
  })
}