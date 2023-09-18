import request from '@/utils/request'

// 查询演出(场次)日期列表
export function listProductTicketSession(query) {
  return request({
    url: '/zlyyh-admin/productTicketSession/list',
    method: 'get',
    params: query
  })
}

// 查询演出(场次)日期详细
export function getProductTicketSession(priceId) {
  return request({
    url: '/zlyyh-admin/productTicketSession/' + priceId,
    method: 'get'
  })
}

// 新增演出(场次)日期
export function addProductTicketSession(data) {
  return request({
    url: '/zlyyh-admin/productTicketSession',
    method: 'post',
    data: data
  })
}

// 修改演出(场次)日期
export function updateProductTicketSession(data) {
  return request({
    url: '/zlyyh-admin/productTicketSession',
    method: 'put',
    data: data
  })
}

// 删除演出(场次)日期
export function delProductTicketSession(priceId) {
  return request({
    url: '/zlyyh-admin/productTicketSession/' + priceId,
    method: 'delete'
  })
}
