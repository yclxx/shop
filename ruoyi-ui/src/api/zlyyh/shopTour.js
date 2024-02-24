import request from '@/utils/request'

// 查询巡检商户列表
export function listShopTour(query) {
  return request({
    url: '/zlyyh-admin/shopTour/list',
    method: 'get',
    params: query
  })
}

// 查询巡检商户详细
export function getShopTour(id) {
  return request({
    url: '/zlyyh-admin/shopTour/' + id,
    method: 'get'
  })
}

// 新增巡检商户
export function addShopTour(data) {
  return request({
    url: '/zlyyh-admin/shopTour',
    method: 'post',
    data: data
  })
}

// 修改巡检商户
export function updateShopTour(data) {
  return request({
    url: '/zlyyh-admin/shopTour',
    method: 'put',
    data: data
  })
}

// 删除巡检商户
export function delShopTour(id) {
  return request({
    url: '/zlyyh-admin/shopTour/' + id,
    method: 'delete'
  })
}

// 添加巡检商户
export function changeTourShop(data) {
  return request({
    url: '/zlyyh-admin/shopTour/changeTourShop',
    method: 'post',
    data: data
  })
}

// 巡检审核通过
export function tourCheckPass(data) {
  return request({
    url: '/zlyyh-admin/shopTour/tourCheckPass',
    method: 'post',
    data: data
  })
}