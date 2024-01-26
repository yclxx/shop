import request from '@/utils/request'

// 查询任务组背景图片配置列表
export function listMissionGroupBgImg(query) {
  return request({
    url: '/zlyyh-admin/missionGroupBgImg/list',
    method: 'get',
    params: query
  })
}

// 查询任务组背景图片配置详细
export function getMissionGroupBgImg(missionGroupId) {
  return request({
    url: '/zlyyh-admin/missionGroupBgImg/' + missionGroupId,
    method: 'get'
  })
}

// 新增任务组背景图片配置
export function addMissionGroupBgImg(data) {
  return request({
    url: '/zlyyh-admin/missionGroupBgImg',
    method: 'post',
    data: data
  })
}

// 修改任务组背景图片配置
export function updateMissionGroupBgImg(data) {
  return request({
    url: '/zlyyh-admin/missionGroupBgImg',
    method: 'put',
    data: data
  })
}

// 删除任务组背景图片配置
export function delMissionGroupBgImg(missionGroupId) {
  return request({
    url: '/zlyyh-admin/missionGroupBgImg/' + missionGroupId,
    method: 'delete'
  })
}
