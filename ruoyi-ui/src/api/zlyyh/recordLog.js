import request from '@/utils/request'

// 查询记录日志列表
export function listRecordLog(query) {
  return request({
    url: '/zlyyh-admin/recordLog/list', method: 'get', params: query
  })
}

// 查询记录日志详细
export function getRecordLog(recordId) {
  return request({
    url: '/zlyyh-admin/recordLog/' + recordId, method: 'get'
  })
}

// 新增记录日志
export function addRecordLog(data) {
  return request({
    url: '/zlyyh-admin/recordLog', method: 'post', data: data
  })
}

// 修改记录日志
export function updateRecordLog(data) {
  return request({
    url: '/zlyyh-admin/recordLog', method: 'put', data: data
  })
}

// 删除记录日志
export function delRecordLog(recordId) {
  return request({
    url: '/zlyyh-admin/recordLog/' + recordId, method: 'delete'
  })
}
