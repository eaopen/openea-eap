import request from '@/utils/request'

/**
 * 获取前端i18n Json
 * @param query
 * @returns {*}
 */
export function getI18nJs(query) {
  return request({
    url: '/system/i18n-data/getJs',
    method: 'get',
    params: query
  })
}
