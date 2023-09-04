import request from '@/utils/request'

// 查询版块模板列表
export function listPageBlock(query) {
  return request({
    url: '/zlyyh-admin/pageBlock/list',
    method: 'get',
    params: query
  })
}

//查询板块模版列表
export function selectListPageBlock(query) {
  return request({
    url: '/zlyyh-admin/pageBlock/selectList',
    method: 'get',
    params: query
  })
}

// 查询版块模板详细
export function getPageBlock(id) {
  return request({
    url: '/zlyyh-admin/pageBlock/' + id,
    method: 'get'
  })
}

// 新增版块模板
export function addPageBlock(data) {
  return request({
    url: '/zlyyh-admin/pageBlock',
    method: 'post',
    data: data
  })
}

// 修改版块模板
export function updatePageBlock(data) {
  return request({
    url: '/zlyyh-admin/pageBlock',
    method: 'put',
    data: data
  })
}

// 删除版块模板
export function delPageBlock(id) {
  return request({
    url: '/zlyyh-admin/pageBlock/' + id,
    method: 'delete'
  })
}
