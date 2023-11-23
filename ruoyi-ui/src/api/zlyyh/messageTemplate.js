import request from '@/utils/request'

// 查询消息模板列表
export function listMessageTemplate(query) {
  return request({
    url: '/zlyyh-admin/messageTemplate/list',
    method: 'get',
    params: query
  })
}

// 查询消息模板详细
export function getMessageTemplate(templateId) {
  return request({
    url: '/zlyyh-admin/messageTemplate/' + templateId,
    method: 'get'
  })
}

// 新增消息模板
export function addMessageTemplate(data) {
  return request({
    url: '/zlyyh-admin/messageTemplate',
    method: 'post',
    data: data
  })
}

// 修改消息模板
export function updateMessageTemplate(data) {
  return request({
    url: '/zlyyh-admin/messageTemplate',
    method: 'put',
    data: data
  })
}

// 删除消息模板
export function delMessageTemplate(templateId) {
  return request({
    url: '/zlyyh-admin/messageTemplate/' + templateId,
    method: 'delete'
  })
}
