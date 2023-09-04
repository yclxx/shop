import request from '@/utils/request'

// 查询秒杀配置列表
export function listGrabPeriod(query) {
  return request({
    url: '/zlyyh-admin/grabPeriod/list',
    method: 'get',
    params: query
  })
}

// 查询秒杀配置下拉列表
export function listSelectGrabPeriod(query) {
  return request({
    url: '/zlyyh-admin/grabPeriod/selectList',
    method: 'get',
    params: query
  })
}

// 查询秒杀配置详细
export function getGrabPeriod(id) {
  return request({
    url: '/zlyyh-admin/grabPeriod/' + id,
    method: 'get'
  })
}

// 新增秒杀配置
export function addGrabPeriod(data) {
  return request({
    url: '/zlyyh-admin/grabPeriod',
    method: 'post',
    data: data
  })
}

// 修改秒杀配置
export function updateGrabPeriod(data) {
  return request({
    url: '/zlyyh-admin/grabPeriod',
    method: 'put',
    data: data
  })
}

// 删除秒杀配置
export function delGrabPeriod(id) {
  return request({
    url: '/zlyyh-admin/grabPeriod/' + id,
    method: 'delete'
  })
}
