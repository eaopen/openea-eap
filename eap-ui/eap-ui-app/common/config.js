module.exports = {
  //后端接口地址
  baseUrl: 'http://127.0.0.1:48080/app-api',
  // baseUrl: 'http://api-dashboard.eap.iocoder.cn/app-api',
  // 超时
  timeout: 30000,
  // 禁用 Cookie 等信息
  withCredentials: false,
  header: {
    //租户ID
    'tenant-id': 1
  }
}
