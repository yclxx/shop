import request from '@/utils/request'

// 查询权益包列表
export function listEquity(query) {
  return request({
    url: '/zlyyh-admin/equity/list',
    method: 'get',
    params: query
  })
}

// 查询权益包列表
export function selectListEquity(query) {
  return request({
    url: '/zlyyh-admin/equity/selectList',
    method: 'get',
    params: query
  })
}

// 查询权益包详细
export function getEquity(equityId) {
  return request({
    url: '/zlyyh-admin/equity/' + equityId,
    method: 'get'
  })
}

// 新增权益包
export function addEquity(data) {
  return request({
    url: '/zlyyh-admin/equity',
    method: 'post',
    data: data
  })
}

// 修改权益包
export function updateEquity(data) {
  return request({
    url: '/zlyyh-admin/equity',
    method: 'put',
    data: data
  })
}

// 删除权益包
export function delEquity(equityId) {
  return request({
    url: '/zlyyh-admin/equity/' + equityId,
    method: 'delete'
  })
}