import request from '@/utils/request'

// 查询演出票种列表
export function listProductTicketLine(query) {
  return request({
    url: '/zlyyh-admin/productTicketLine/list',
    method: 'get',
    params: query
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
