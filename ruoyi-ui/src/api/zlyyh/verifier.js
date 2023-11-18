import request from '@/utils/request'

// 查询核销人员管理列表
export function listVerifier(query) {
  return request({
    url: '/zlyyh-admin/verifier/list',
    method: 'get',
    params: query
  })
}

// 查询核销人员管理详细
export function getVerifier(id) {
  return request({
    url: '/zlyyh-admin/verifier/' + id,
    method: 'get'
  })
}

// 新增核销人员管理
export function addVerifier(data) {
  return request({
    url: '/zlyyh-admin/verifier',
    method: 'post',
    data: data
  })
}

// 修改核销人员管理
export function updateVerifier(data) {
  return request({
    url: '/zlyyh-admin/verifier',
    method: 'put',
    data: data
  })
}

// 删除核销人员管理
export function delVerifier(id) {
  return request({
    url: '/zlyyh-admin/verifier/' + id,
    method: 'delete'
  })
}
