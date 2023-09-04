import request from '@/utils/request'

// 查询领取记录列表
export function listEquityRecord(query) {
  return request({
    url: '/zlyyh-admin/equityRecord/list',
    method: 'get',
    params: query
  })
}

// 查询领取记录详细
export function getEquityRecord(id) {
  return request({
    url: '/zlyyh-admin/equityRecord/' + id,
    method: 'get'
  })
}

// 新增领取记录
export function addEquityRecord(data) {
  return request({
    url: '/zlyyh-admin/equityRecord',
    method: 'post',
    data: data
  })
}

// 修改领取记录
export function updateEquityRecord(data) {
  return request({
    url: '/zlyyh-admin/equityRecord',
    method: 'put',
    data: data
  })
}

// 删除领取记录
export function delEquityRecord(id) {
  return request({
    url: '/zlyyh-admin/equityRecord/' + id,
    method: 'delete'
  })
}