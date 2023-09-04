import request from '@/utils/request'

// 查询活动订单取码记录列表
export function listMissionUserRecordLog(query) {
  return request({
    url: '/zlyyh-admin/missionUserRecordLog/list',
    method: 'get',
    params: query
  })
}

// 查询活动订单取码记录详细
export function getMissionUserRecordLog(id) {
  return request({
    url: '/zlyyh-admin/missionUserRecordLog/' + id,
    method: 'get'
  })
}

// 新增活动订单取码记录
export function addMissionUserRecordLog(data) {
  return request({
    url: '/zlyyh-admin/missionUserRecordLog',
    method: 'post',
    data: data
  })
}

// 修改活动订单取码记录
export function updateMissionUserRecordLog(data) {
  return request({
    url: '/zlyyh-admin/missionUserRecordLog',
    method: 'put',
    data: data
  })
}

// 删除活动订单取码记录
export function delMissionUserRecordLog(id) {
  return request({
    url: '/zlyyh-admin/missionUserRecordLog/' + id,
    method: 'delete'
  })
}