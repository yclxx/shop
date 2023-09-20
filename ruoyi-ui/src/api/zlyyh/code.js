import request from '@/utils/request'

// 查询商品券码列表
export function listCode(query) {
  return request({
    url: '/zlyyh/code/list',
    method: 'get',
    params: query
  })
}

// 查询商品券码详细
export function getCode(id) {
  return request({
    url: '/zlyyh/code/' + id,
    method: 'get'
  })
}

// 新增商品券码
export function addCode(data) {
  return request({
    url: '/zlyyh/code',
    method: 'post',
    data: data
  })
}

// 修改商品券码
export function updateCode(data) {
  return request({
    url: '/zlyyh/code',
    method: 'put',
    data: data
  })
}

// 删除商品券码
export function delCode(id) {
  return request({
    url: '/zlyyh/code/' + id,
    method: 'delete'
  })
}
