import request from '@/utils/request'

// 查询用户地址列表
export function listUserAddress(query) {
  return request({
    url: '/zlyyh-admin/userAddress/list',
    method: 'get',
    params: query
  })
}

// 查询用户地址详细
export function getUserAddress(userAddressId) {
  return request({
    url: '/zlyyh-admin/userAddress/' + userAddressId,
    method: 'get'
  })
}

// 新增用户地址
export function addUserAddress(data) {
  return request({
    url: '/zlyyh-admin/userAddress',
    method: 'post',
    data: data
  })
}

// 修改用户地址
export function updateUserAddress(data) {
  return request({
    url: '/zlyyh-admin/userAddress',
    method: 'put',
    data: data
  })
}

// 删除用户地址
export function delUserAddress(userAddressId) {
  return request({
    url: '/zlyyh-admin/userAddress/' + userAddressId,
    method: 'delete'
  })
}
