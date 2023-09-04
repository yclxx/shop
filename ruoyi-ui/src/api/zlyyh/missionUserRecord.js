import request from '@/utils/request'

// 查询活动记录列表
export function listMissionUserRecord(query) {
  return request({
    url: '/zlyyh-admin/missionUserRecord/list',
    method: 'get',
    params: query
  })
}

// 查询活动记录详细
export function getMissionUserRecord(missionUserRecordId) {
  return request({
    url: '/zlyyh-admin/missionUserRecord/' + missionUserRecordId,
    method: 'get'
  })
}

// 新增活动记录
export function addMissionUserRecord(data) {
  return request({
    url: '/zlyyh-admin/missionUserRecord',
    method: 'post',
    data: data
  })
}

// 修改活动记录
export function updateMissionUserRecord(data) {
  return request({
    url: '/zlyyh-admin/missionUserRecord',
    method: 'put',
    data: data
  })
}

// 作废活动记录
export function expiryMissionUserRecord(missionUserRecordId) {
  return request({
    url: '/zlyyh-admin/missionUserRecord/expiry/' + missionUserRecordId,
    method: 'put'
  })
}

// 删除活动记录
export function delMissionUserRecord(missionUserRecordId) {
  return request({
    url: '/zlyyh-admin/missionUserRecord/' + missionUserRecordId,
    method: 'delete'
  })
}

// 订单补发
export function reissue(missionUserRecordId) {
  return request({
    url: '/zlyyh-admin/missionUserRecord/reissue/' + missionUserRecordId,
    method: 'get'
  })
}