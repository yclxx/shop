import request from '@/utils/request'

// 查询分销记录列表
export function listShareUserRecord(query) {
  return request({
    url: '/zlyyh/shareUserRecord/list',
    method: 'get',
    params: query
  })
}

// 查询分销记录详细
export function getShareUserRecord(recordId) {
  return request({
    url: '/zlyyh/shareUserRecord/' + recordId,
    method: 'get'
  })
}

// 新增分销记录
export function addShareUserRecord(data) {
  return request({
    url: '/zlyyh/shareUserRecord',
    method: 'post',
    data: data
  })
}

// 修改分销记录
export function updateShareUserRecord(data) {
  return request({
    url: '/zlyyh/shareUserRecord',
    method: 'put',
    data: data
  })
}

// 删除分销记录
export function delShareUserRecord(recordId) {
  return request({
    url: '/zlyyh/shareUserRecord/' + recordId,
    method: 'delete'
  })
}
