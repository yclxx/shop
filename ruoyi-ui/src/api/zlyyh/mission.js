import request from '@/utils/request'

// 查询任务配置列表
export function listMission(query) {
  return request({
    url: '/zlyyh-admin/mission/list',
    method: 'get',
    params: query
  })
}

// 查询任务组列表
export function listMissionSelect(query) {
  return request({
    url: '/zlyyh-admin/mission/selectList',
    method: 'get',
    params: query
  })
}

// 查询任务配置详细
export function getMission(missionId) {
  return request({
    url: '/zlyyh-admin/mission/' + missionId,
    method: 'get'
  })
}

// 新增任务配置
export function addMission(data) {
  return request({
    url: '/zlyyh-admin/mission',
    method: 'post',
    data: data
  })
}

// 修改任务配置
export function updateMission(data) {
  return request({
    url: '/zlyyh-admin/mission',
    method: 'put',
    data: data
  })
}

// 删除任务配置
export function delMission(missionId) {
  return request({
    url: '/zlyyh-admin/mission/' + missionId,
    method: 'delete'
  })
}