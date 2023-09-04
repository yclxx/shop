import request from '@/utils/request'

// 查询秒杀商品配置列表
export function listGrabPeriodProduct(query) {
  return request({
    url: '/zlyyh-admin/grabPeriodProduct/list',
    method: 'get',
    params: query
  })
}

// 查询秒杀商品配置详细
export function getGrabPeriodProduct(id) {
  return request({
    url: '/zlyyh-admin/grabPeriodProduct/' + id,
    method: 'get'
  })
}

// 新增秒杀商品配置
export function addGrabPeriodProduct(data) {
  return request({
    url: '/zlyyh-admin/grabPeriodProduct',
    method: 'post',
    data: data
  })
}

// 修改秒杀商品配置
export function updateGrabPeriodProduct(data) {
  return request({
    url: '/zlyyh-admin/grabPeriodProduct',
    method: 'put',
    data: data
  })
}

// 删除秒杀商品配置
export function delGrabPeriodProduct(id) {
  return request({
    url: '/zlyyh-admin/grabPeriodProduct/' + id,
    method: 'delete'
  })
}
