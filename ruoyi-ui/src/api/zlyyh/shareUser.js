import request from '@/utils/request'

// 查询分销员列表
export function listShareUser(query) {
  return request({
    url: '/zlyyh/shareUser/list',
    method: 'get',
    params: query
  })
}

// 查询分销员详细
export function getShareUser(userId) {
  return request({
    url: '/zlyyh/shareUser/' + userId,
    method: 'get'
  })
}

// 新增分销员
export function addShareUser(data) {
  return request({
    url: '/zlyyh/shareUser',
    method: 'post',
    data: data
  })
}

// 修改分销员
export function updateShareUser(data) {
  return request({
    url: '/zlyyh/shareUser',
    method: 'put',
    data: data
  })
}

// 删除分销员
export function delShareUser(userId) {
  return request({
    url: '/zlyyh/shareUser/' + userId,
    method: 'delete'
  })
}
