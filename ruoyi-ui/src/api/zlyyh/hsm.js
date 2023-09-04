import request from '@/utils/request'

// 加密
export function encrypt(data) {
  return request({
    url: '/zlyyh-admin/hsm',
    method: 'post',
    data: data
  })
}