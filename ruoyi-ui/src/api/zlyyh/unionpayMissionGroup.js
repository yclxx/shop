import request from '@/utils/request'

// 查询银联任务组列表
export function listUnionpayMissionGroup(query) {
  return request({
    url: '/zlyyh-admin/unionpayMissionGroup/list',
    method: 'get',
    params: query
  })
}

// 查询银联任务组详细
export function getUnionpayMissionGroup(upMissionGroupId) {
  return request({
    url: '/zlyyh-admin/unionpayMissionGroup/' + upMissionGroupId,
    method: 'get'
  })
}

// 新增银联任务组
export function addUnionpayMissionGroup(data) {
  return request({
    url: '/zlyyh-admin/unionpayMissionGroup',
    method: 'post',
    data: data
  })
}

// 修改银联任务组
export function updateUnionpayMissionGroup(data) {
  return request({
    url: '/zlyyh-admin/unionpayMissionGroup',
    method: 'put',
    data: data
  })
}

// 删除银联任务组
export function delUnionpayMissionGroup(upMissionGroupId) {
  return request({
    url: '/zlyyh-admin/unionpayMissionGroup/' + upMissionGroupId,
    method: 'delete'
  })
}

// 查询任务组下拉列表
export function selectListMissionGroup(query) {
  return request({
    url: '/zlyyh-admin/unionpayMissionGroup/selectMissionGroupList',
    method: 'get',
    params: query
  })
}