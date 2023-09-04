import request from '@/utils/request'

// 查询页面配置列表
export function listPageSetting(query) {
  return request({
    url: '/zlyyh-admin/pageSetting/list',
    method: 'get',
    params: query
  })
}

// 查询页面配置详细
export function getPageSetting(id) {
  return request({
    url: '/zlyyh-admin/pageSetting/' + id,
    method: 'get'
  })
}

// 新增页面配置
export function addPageSetting(data) {
  return request({
    url: '/zlyyh-admin/pageSetting',
    method: 'post',
    data: data
  })
}

// 修改页面配置
export function updatePageSetting(data) {
  return request({
    url: '/zlyyh-admin/pageSetting',
    method: 'put',
    data: data
  })
}

// 删除页面配置
export function delPageSetting(id) {
  return request({
    url: '/zlyyh-admin/pageSetting/' + id,
    method: 'delete'
  })
}
