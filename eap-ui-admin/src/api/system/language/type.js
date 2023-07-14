import request from '@/utils/request'

// 创建语言
export function createLangType(data) {
  return request({
    url: '/system/lang-type/create',
    method: 'post',
    data: data
  })
}

// 更新语言
export function updateLangType(data) {
  return request({
    url: '/system/lang-type/update',
    method: 'put',
    data: data
  })
}

// 删除语言
export function deleteLangType(id) {
  return request({
    url: '/system/lang-type/delete?id=' + id,
    method: 'delete'
  })
}

// 获得语言
export function getLangType(id) {
  return request({
    url: '/system/lang-type/get?id=' + id,
    method: 'get'
  })
}

// 获得语言分页
export function getLangTypePage(query) {
  return request({
    url: '/system/lang-type/page',
    method: 'get',
    params: query
  })
}

// 导出语言 Excel
export function exportLangTypeExcel(query) {
  return request({
    url: '/system/lang-type/export-excel',
    method: 'get',
    params: query,
    responseType: 'blob'
  })
}
