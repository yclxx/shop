import request from '@/utils/request'

// 查询搜索彩蛋配置列表
export function listSearchGroup(query) {
  return request({
    url: '/zlyyh-admin/searchGroup/list',
    method: 'get',
    params: query
  })
}

// 查询搜索彩蛋配置详细
export function getSearchGroup(searchId) {
  return request({
    url: '/zlyyh-admin/searchGroup/' + searchId,
    method: 'get'
  })
}

// 新增搜索彩蛋配置
export function addSearchGroup(data) {
  return request({
    url: '/zlyyh-admin/searchGroup',
    method: 'post',
    data: data
  })
}

// 修改搜索彩蛋配置
export function updateSearchGroup(data) {
  return request({
    url: '/zlyyh-admin/searchGroup',
    method: 'put',
    data: data
  })
}

// 删除搜索彩蛋配置
export function delSearchGroup(searchId) {
  return request({
    url: '/zlyyh-admin/searchGroup/' + searchId,
    method: 'delete'
  })
}
