import request from '@/utils/request'

// 查询供应商参数配置列表
export function listSupplierConfig(query) {
  return request({
    url: '/zlyyh-admin/supplierConfig/list',
    method: 'get',
    params: query
  })
}

// 查询供应商参数配置详细
export function getSupplierConfig(configId) {
  return request({
    url: '/zlyyh-admin/supplierConfig/' + configId,
    method: 'get'
  })
}

// 新增供应商参数配置
export function addSupplierConfig(data) {
  return request({
    url: '/zlyyh-admin/supplierConfig',
    method: 'post',
    data: data
  })
}

// 修改供应商参数配置
export function updateSupplierConfig(data) {
  return request({
    url: '/zlyyh-admin/supplierConfig',
    method: 'put',
    data: data
  })
}

// 删除供应商参数配置
export function delSupplierConfig(configId) {
  return request({
    url: '/zlyyh-admin/supplierConfig/' + configId,
    method: 'delete'
  })
}
