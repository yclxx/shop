import request from '@/utils/request'

// 查询联联市级城市列表
export function listLianlianCity(query) {
  return request({
    url: '/zlyyh-admin/lianlianCity/list',
    method: 'get',
    params: query
  })
}

// 查询联联市级城市详细
export function getLianlianCity(cityId) {
  return request({
    url: '/zlyyh-admin/lianlianCity/' + cityId,
    method: 'get'
  })
}

// 新增联联市级城市
export function addLianlianCity(data) {
  return request({
    url: '/zlyyh-admin/lianlianCity',
    method: 'post',
    data: data
  })
}

// 修改联联市级城市
export function updateLianlianCity(data) {
  return request({
    url: '/zlyyh-admin/lianlianCity',
    method: 'put',
    data: data
  })
}

// 删除联联市级城市
export function delLianlianCity(cityId) {
  return request({
    url: '/zlyyh-admin/lianlianCity/' + cityId,
    method: 'delete'
  })
}
