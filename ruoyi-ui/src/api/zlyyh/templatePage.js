import request from '@/utils/request'

// 查询落地页列表
export function listTemplatePage(query) {
  return request({
    url: '/zlyyh-admin/templatePage/list',
    method: 'get',
    params: query
  })
}

// 查询下拉列表
export function selectListTemplatePage(query) {
  return request({
    url: '/zlyyh-admin/templatePage/selectList',
    method: 'get',
    params: query
  })
}

// 查询落地页详细
export function getTemplatePage(templateId) {
  return request({
    url: '/zlyyh-admin/templatePage/' + templateId,
    method: 'get'
  })
}

// 新增落地页
export function addTemplatePage(data) {
  return request({
    url: '/zlyyh-admin/templatePage',
    method: 'post',
    data: data
  })
}

// 修改落地页
export function updateTemplatePage(data) {
  return request({
    url: '/zlyyh-admin/templatePage',
    method: 'put',
    data: data
  })
}

// 删除落地页
export function delTemplatePage(templateId) {
  return request({
    url: '/zlyyh-admin/templatePage/' + templateId,
    method: 'delete'
  })
}