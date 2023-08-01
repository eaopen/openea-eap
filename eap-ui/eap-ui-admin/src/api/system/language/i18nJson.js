import request from '@/utils/request'

// 创建翻译
export function createI18nJsonData(data) {
  return request({
    url: '/system/I18n-json-data/create',
    method: 'post',
    data: data
  })
}

// 更新翻译
export function updateI18nJsonData(data) {
  return request({
    url: '/system/I18n-json-data/update',
    method: 'put',
    data: data
  })
}

// 删除翻译
export function deleteI18nJsonData(id) {
  return request({
    url: '/system/I18n-json-data/delete?id=' + id,
    method: 'delete'
  })
}

// 获得翻译
export function getI18nJsonData(id) {
  return request({
    url: '/system/I18n-json-data/get?id=' + id,
    method: 'get'
  })
}

// 获得翻译分页
export function getI18nJsonDataPage(query) {
  return request({
    url: '/system/I18n-json-data/page',
    method: 'get',
    params: query
  })
}

// 导出翻译 Excel
export function exportI18nJsonDataExcel(query) {
  return request({
    url: '/system/I18n-json-data/export-excel',
    method: 'get',
    params: query,
    responseType: 'blob'
  })
}
