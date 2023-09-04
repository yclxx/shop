import request from '@/utils/request'

// 查询页面列表
export function listPage(query) {
  return request({
    url: '/zlyyh-admin/page/list',
    method: 'get',
    params: query
  })
}

// 查询页面详细
export function getPage(id) {
  return request({
    url: '/zlyyh-admin/page/' + id,
    method: 'get'
  })
}

// 新增页面
export function addPage(data) {
  return request({
    url: '/zlyyh-admin/page',
    method: 'post',
    data: data
  })
}

// 修改页面
export function updatePage(data) {
  return request({
    url: '/zlyyh-admin/page',
    method: 'put',
    data: data
  })
}

// 删除页面
export function delPage(id) {
  return request({
    url: '/zlyyh-admin/page/' + id,
    method: 'delete'
  })
}

//查询page列表
export function selectListPage(query) {
  return request({
    url: '/zlyyh-admin/page/selectListPage',
    method: 'get',
    params: query
  })
}
