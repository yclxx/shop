import request from '@/utils/request'

// 查询银联任务奖励发放记录列表
export function listUnionpayMissionUserLog(query) {
  return request({
    url: '/zlyyh-admin/unionpayMissionUserLog/list',
    method: 'get',
    params: query
  })
}

// 查询银联任务奖励发放记录详细
export function getUnionpayMissionUserLog(upMissionUserLog) {
  return request({
    url: '/zlyyh-admin/unionpayMissionUserLog/' + upMissionUserLog,
    method: 'get'
  })
}

// 新增银联任务奖励发放记录
export function addUnionpayMissionUserLog(data) {
  return request({
    url: '/zlyyh-admin/unionpayMissionUserLog',
    method: 'post',
    data: data
  })
}

// 修改银联任务奖励发放记录
export function updateUnionpayMissionUserLog(data) {
  return request({
    url: '/zlyyh-admin/unionpayMissionUserLog',
    method: 'put',
    data: data
  })
}

// 删除银联任务奖励发放记录
export function delUnionpayMissionUserLog(upMissionUserLog) {
  return request({
    url: '/zlyyh-admin/unionpayMissionUserLog/' + upMissionUserLog,
    method: 'delete'
  })
}