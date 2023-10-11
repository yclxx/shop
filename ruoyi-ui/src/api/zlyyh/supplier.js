import request from '@/utils/request'

// 查询供应商列表
export function listSupplier(query) {
  return request({
    url: '/zlyyh-admin/supplier/list',
    method: 'get',
    params: query
  })
}

// 查询供应商列表
export function selectSupplier(query) {
  return request({
    url: '/zlyyh-admin/supplier/select',
    method: 'get',
    params: query
  })
}

// 查询供应商详细
export function getSupplier(supplierId) {
  return request({
    url: '/zlyyh-admin/supplier/' + supplierId,
    method: 'get'
  })
}

// 新增供应商
export function addSupplier(data) {
  return request({
    url: '/zlyyh-admin/supplier',
    method: 'post',
    data: data
  })
}

// 修改供应商
export function updateSupplier(data) {
  return request({
    url: '/zlyyh-admin/supplier',
    method: 'put',
    data: data
  })
}

// 删除供应商
export function delSupplier(supplierId) {
  return request({
    url: '/zlyyh-admin/supplier/' + supplierId,
    method: 'delete'
  })
}
