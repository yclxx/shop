import request from '@/utils/request'

// 查询演出票种列表
export function listProductTicketLine(query) {
  return request({
    url: '/zlyyh-admin/productTicketLine/list',
    method: 'get',
    params: query
  })
}

// 新增演出票种
export function addProductTicketLine(data) {
  return request({
    url: '/zlyyh-admin/productTicketLine',
    method: 'post',
    data: data
  })
}

// 修改演出票种
export function updateProductTicketLine(data) {
// 查询观影人列表
  return request({
    url: '/zlyyh-admin/productTicketLine',
    method: 'put',
    data: data
  })
}

// 查询演出票种详细
export function getProductTicketLine(lineId) {
  return request({
    url: '/zlyyh-admin/productTicketLine/' + lineId,
    method: 'get'
  })
}

// 查询观影人列表
export function getOrderIdCardList(lineId) {
  return request({
    url: '/zlyyh-admin/orderTicket/getOrderIdCardList/' + lineId,
    method: 'get'
  })
}


// 删除演出票种
export function delProductTicketLine(lineId) {
  return request({
    url: '/zlyyh-admin/productTicketLine/' + lineId,
    method: 'delete'
  })
}

