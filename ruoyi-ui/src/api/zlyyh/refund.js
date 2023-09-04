import request from '@/utils/request'

// 查询退款订单登记列表
export function listRefund(query) {
  return request({
    url: '/zlyyh-admin/refund/list',
    method: 'get',
    params: query
  })
}

// 查询退款订单登记详细
export function getRefund(refundId) {
  return request({
    url: '/zlyyh-admin/refund/' + refundId,
    method: 'get'
  })
}

// 新增退款订单登记
export function addRefund(data) {
  return request({
    url: '/zlyyh-admin/refund',
    method: 'post',
    data: data
  })
}

// 修改退款订单登记
export function updateRefund(data) {
  return request({
    url: '/zlyyh-admin/refund',
    method: 'put',
    data: data
  })
}

// 删除退款订单登记
export function delRefund(refundId) {
  return request({
    url: '/zlyyh-admin/refund/' + refundId,
    method: 'delete'
  })
}


//审核拒绝
export function refuseSubmit(refundId) {
  let data = {
    refundId
  }
  return request({
    url: '/zlyyh-admin/refund/refuseSubmit',
    method: 'post',
    params: data
  })
}

//审核通过
export function agreeSubmit(refundId) {
  let data = {
    refundId
  }
  return request({
    url: '/zlyyh-admin/refund/agreeSubmit',
    method: 'post',
    params: data
  })
}
