import request from '@/utils/request'

// 查询商户申请审批列表
export function listMerchantApproval(query) {
  return request({
    url: '/zlyyh-admin/merchantApproval/list',
    method: 'get',
    params: query
  })
}

// 查询商户申请审批详细
export function getMerchantApproval(approvalId) {
  return request({
    url: '/zlyyh-admin/merchantApproval/' + approvalId,
    method: 'get'
  })
}

// 修改商户申请审批
export function updateMerchantApproval(data) {
  return request({
    url: '/zlyyh-admin/merchantApproval',
    method: 'put',
    data: data
  })
}
