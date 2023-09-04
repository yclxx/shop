import request from '@/utils/request'

// 查询银联分销订单卡券列表
export function listOrderUnionSend(query) {
  return request({
    url: '/zlyyh-admin/orderUnionSend/list',
    method: 'get',
    params: query
  })
}

// 查询银联分销订单卡券详细
export function getOrderUnionSend(number) {
  return request({
    url: '/zlyyh-admin/orderUnionSend/' + number,
    method: 'get'
  })
}

// 新增银联分销订单卡券
export function addOrderUnionSend(data) {
  return request({
    url: '/zlyyh-admin/orderUnionSend',
    method: 'post',
    data: data
  })
}

// 修改银联分销订单卡券
export function updateOrderUnionSend(data) {
  return request({
    url: '/zlyyh-admin/orderUnionSend',
    method: 'put',
    data: data
  })
}

// 删除银联分销订单卡券
export function delOrderUnionSend(number) {
  return request({
    url: '/zlyyh-admin/orderUnionSend/' + number,
    method: 'delete'
  })
}
