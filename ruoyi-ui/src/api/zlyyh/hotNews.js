import request from '@/utils/request'

// 查询热门搜索配置列表
export function listHotNews(query) {
  return request({
    url: '/zlyyh-admin/hotNews/list',
    method: 'get',
    params: query
  })
}

// 查询热门搜索配置详细
export function getHotNews(newsId) {
  return request({
    url: '/zlyyh-admin/hotNews/' + newsId,
    method: 'get'
  })
}

// 新增热门搜索配置
export function addHotNews(data) {
  return request({
    url: '/zlyyh-admin/hotNews',
    method: 'post',
    data: data
  })
}

// 修改热门搜索配置
export function updateHotNews(data) {
  return request({
    url: '/zlyyh-admin/hotNews',
    method: 'put',
    data: data
  })
}

// 删除热门搜索配置
export function delHotNews(newsId) {
  return request({
    url: '/zlyyh-admin/hotNews/' + newsId,
    method: 'delete'
  })
}
