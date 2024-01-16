import request from '@/utils/request'

// 查询分销员列表
export function pageListShareUser(query) {
  return request({
    url: '/zlyyh-admin/shareUser/pageList',
    method: 'get',
    params: query
  })
}

// 查询分销员列表
export function listShareUser(query) {
  return request({
    url: '/zlyyh-admin/shareUser/list',
    method: 'get',
    params: query
  })
}

// 查询分销员详细
export function getShareUser(userId) {
  return request({
    url: '/zlyyh-admin/shareUser/' + userId,
    method: 'get'
  })
}

// 新增分销员
export function addShareUser(data) {
  return request({
    url: '/zlyyh-admin/shareUser',
    method: 'post',
    data: data
  })
}

// 修改分销员
export function updateShareUser(data) {
  return request({
    url: '/zlyyh-admin/shareUser',
    method: 'put',
    data: data
  })
}

// 删除分销员
export function delShareUser(userId) {
  return request({
    url: '/zlyyh-admin/shareUser/' + userId,
    method: 'delete'
  })
}
