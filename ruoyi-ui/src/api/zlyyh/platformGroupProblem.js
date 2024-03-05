import request from '@/utils/request'

// 查询用户入群问题反馈列表
export function listPlatformGroupProblem(query) {
  return request({
    url: '/zlyyh/platformGroupProblem/list',
    method: 'get',
    params: query
  })
}

// 查询用户入群问题反馈详细
export function getPlatformGroupProblem(id) {
  return request({
    url: '/zlyyh/platformGroupProblem/' + id,
    method: 'get'
  })
}

// 新增用户入群问题反馈
export function addPlatformGroupProblem(data) {
  return request({
    url: '/zlyyh/platformGroupProblem',
    method: 'post',
    data: data
  })
}

// 修改用户入群问题反馈
export function updatePlatformGroupProblem(data) {
  return request({
    url: '/zlyyh/platformGroupProblem',
    method: 'put',
    data: data
  })
}

// 删除用户入群问题反馈
export function delPlatformGroupProblem(id) {
  return request({
    url: '/zlyyh/platformGroupProblem/' + id,
    method: 'delete'
  })
}
