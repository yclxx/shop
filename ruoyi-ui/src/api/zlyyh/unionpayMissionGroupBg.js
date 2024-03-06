import request from '@/utils/request'

// 查询任务组背景列表
export function listUnionpayMissionGroupBg(query) {
  return request({
    url: '/zlyyh-admin/unionpayMissionGroupBg/list',
    method: 'get',
    params: query
  })
}

// 查询任务组背景详细
export function getUnionpayMissionGroupBg(missionBgId) {
  return request({
    url: '/zlyyh-admin/unionpayMissionGroupBg/' + missionBgId,
    method: 'get'
  })
}

// 新增任务组背景
export function addUnionpayMissionGroupBg(data) {
  return request({
    url: '/zlyyh-admin/unionpayMissionGroupBg',
    method: 'post',
    data: data
  })
}

// 修改任务组背景
export function updateUnionpayMissionGroupBg(data) {
  return request({
    url: '/zlyyh-admin/unionpayMissionGroupBg',
    method: 'put',
    data: data
  })
}

// 删除任务组背景
export function delUnionpayMissionGroupBg(missionBgId) {
  return request({
    url: '/zlyyh-admin/unionpayMissionGroupBg/' + missionBgId,
    method: 'delete'
  })
}