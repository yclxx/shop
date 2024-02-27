import request from '@/utils/request'

// 查询银联任务配置列表
export function listUnionpayMission(query) {
  return request({
    url: '/zlyyh-admin/unionpayMission/list',
    method: 'get',
    params: query
  })
}

// 查询银联任务配置详细
export function getUnionpayMission(upMissionId) {
  return request({
    url: '/zlyyh-admin/unionpayMission/' + upMissionId,
    method: 'get'
  })
}

// 新增银联任务配置
export function addUnionpayMission(data) {
  return request({
    url: '/zlyyh-admin/unionpayMission',
    method: 'post',
    data: data
  })
}

// 修改银联任务配置
export function updateUnionpayMission(data) {
  return request({
    url: '/zlyyh-admin/unionpayMission',
    method: 'put',
    data: data
  })
}

// 删除银联任务配置
export function delUnionpayMission(upMissionId) {
  return request({
    url: '/zlyyh-admin/unionpayMission/' + upMissionId,
    method: 'delete'
  })
}

// 查询任务下拉信息列表
export function selectMissionList(query) {
  return request({
    url: '/zlyyh-admin/unionpayMission/selectMissionList',
    method: 'get',
    params: query
  })
}