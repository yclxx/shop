import request from '@/utils/request'

// 查询银联任务进度列表
export function listUnionpayMissionProgress(query) {
  return request({
    url: '/zlyyh-admin/unionpayMissionProgress/list',
    method: 'get',
    params: query
  })
}

// 查询银联任务进度详细
export function getUnionpayMissionProgress(progressId) {
  return request({
    url: '/zlyyh-admin/unionpayMissionProgress/' + progressId,
    method: 'get'
  })
}

// 新增银联任务进度
export function addUnionpayMissionProgress(data) {
  return request({
    url: '/zlyyh-admin/unionpayMissionProgress',
    method: 'post',
    data: data
  })
}

// 修改银联任务进度
export function updateUnionpayMissionProgress(data) {
  return request({
    url: '/zlyyh-admin/unionpayMissionProgress',
    method: 'put',
    data: data
  })
}

// 删除银联任务进度
export function delUnionpayMissionProgress(progressId) {
  return request({
    url: '/zlyyh-admin/unionpayMissionProgress/' + progressId,
    method: 'delete'
  })
}