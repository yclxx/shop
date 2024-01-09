import request from '@/utils/request'

// 查询文件导入记录列表
export function listFileImportLog(query) {
  return request({
    url: '/zlyyh-admin/fileImportLog/list',
    method: 'get',
    params: query
  })
}

// 查询文件导入记录详细
export function getFileImportLog(importLogId) {
  return request({
    url: '/zlyyh-admin/fileImportLog/' + importLogId,
    method: 'get'
  })
}

// 新增文件导入记录
export function addFileImportLog(data) {
  return request({
    url: '/zlyyh-admin/fileImportLog',
    method: 'post',
    data: data
  })
}

// 修改文件导入记录
export function updateFileImportLog(data) {
  return request({
    url: '/zlyyh-admin/fileImportLog',
    method: 'put',
    data: data
  })
}

// 删除文件导入记录
export function delFileImportLog(importLogId) {
  return request({
    url: '/zlyyh-admin/fileImportLog/' + importLogId,
    method: 'delete'
  })
}

// 查询文件下拉信息列表
export function selectFileList(query) {
  return request({
    url: '/zlyyh-admin/fileImportLog/selectFileList',
    method: 'get',
    params: query
  })
}