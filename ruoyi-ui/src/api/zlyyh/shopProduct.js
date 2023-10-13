import request from '@/utils/request'

export function addShopByProduct(data) {
  return request({
    url: '/zlyyh-admin/shopProduct/addShopByProduct',
    method: 'post',
    data: data
  })
}

export function delByShopProduct(data) {
  return request({
    url: '/zlyyh-admin/shopProduct/delByShopProduct',
    method: 'post',
    data: data
  })
}
