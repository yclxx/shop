import request from '@/utils/request'

// 查询巡检记录列表
export function listShopTourLog(query) {
  return request({
    url: '/zlyyh-admin/shopTourLog/list',
    method: 'get',
    params: query
  })
}

// 查询巡检记录详细
export function getShopTourLog(tourLogId) {
  return request({
    url: '/zlyyh-admin/shopTourLog/' + tourLogId,
    method: 'get'
  })
}

// 新增巡检记录
export function addShopTourLog(data) {
  return request({
    url: '/zlyyh-admin/shopTourLog',
    method: 'post',
    data: data
  })
}

// 修改巡检记录
export function updateShopTourLog(data) {
  return request({
    url: '/zlyyh-admin/shopTourLog',
    method: 'put',
    data: data
  })
}

// 删除巡检记录
export function delShopTourLog(tourLogId) {
  return request({
    url: '/zlyyh-admin/shopTourLog/' + tourLogId,
    method: 'delete'
  })
}