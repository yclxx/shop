import request from '@/utils/request'

// 查询版块模板字段列表
export function listPageBlockColumn(query) {
  return request({
    url: '/zlyyh-admin/pageBlockColumn/list',
    method: 'get',
    params: query
  })
}

// 查询版块模板字段详细
export function getPageBlockColumn(columnId) {
  return request({
    url: '/zlyyh-admin/pageBlockColumn/' + columnId,
    method: 'get'
  })
}

//查询模板字段
export function getPageBlockColumnByBlockId(blockId) {
  return request({
    url: '/zlyyh-admin/pageBlockColumn/selectListByBlockId/' + blockId,
    method: 'get'
  })
}

// 新增版块模板字段
export function addPageBlockColumn(data) {
  return request({
    url: '/zlyyh-admin/pageBlockColumn',
    method: 'post',
    data: data
  })
}

// 修改版块模板字段
export function updatePageBlockColumn(data) {
  return request({
    url: '/zlyyh-admin/pageBlockColumn',
    method: 'put',
    data: data
  })
}

// 删除版块模板字段
export function delPageBlockColumn(columnId) {
  return request({
    url: '/zlyyh-admin/pageBlockColumn/' + columnId,
    method: 'delete'
  })
}
