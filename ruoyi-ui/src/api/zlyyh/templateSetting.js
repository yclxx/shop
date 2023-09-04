import request from '@/utils/request'

// 查询落地页配置列表
export function listTemplateSetting(query) {
  return request({
    url: '/zlyyh-admin/templateSetting/list',
    method: 'get',
    params: query
  })
}

// 查询落地页配置详细
export function getTemplateSetting(id) {
  return request({
    url: '/zlyyh-admin/templateSetting/' + id,
    method: 'get'
  })
}

// 新增落地页配置
export function addTemplateSetting(data) {
  return request({
    url: '/zlyyh-admin/templateSetting',
    method: 'post',
    data: data
  })
}

// 修改落地页配置
export function updateTemplateSetting(data) {
  return request({
    url: '/zlyyh-admin/templateSetting',
    method: 'put',
    data: data
  })
}

// 删除落地页配置
export function delTemplateSetting(id) {
  return request({
    url: '/zlyyh-admin/templateSetting/' + id,
    method: 'delete'
  })
}