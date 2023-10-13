import request from '@/utils/request'

// 查询标签列表
export function listTags(query) {
  return request({
    url: '/zlyyh-admin/tags/list',
    method: 'get',
    params: query
  })
}

// 查询标签列表
export function exportTags(query) {
  return request({
    url: 'zlyyh-admin/tags/export',
    method: 'post',
    params: query
  })
}

// 查询标签详细
export function getTags(tagsId) {
  return request({
    url: '/zlyyh-admin/tags/' + tagsId,
    method: 'get'
  })
}

// 新增标签
export function addTags(data) {
  return request({
    url: '/zlyyh-admin/tags',
    method: 'post',
    data: data
  })
}

// 修改标签
export function updateTags(data) {
  return request({
    url: '/zlyyh-admin/tags',
    method: 'put',
    data: data
  })
}

// 删除标签
export function delTags(tagsId) {
  return request({
    url: '/zlyyh-admin/tags/' + tagsId,
    method: 'delete'
  })
}
