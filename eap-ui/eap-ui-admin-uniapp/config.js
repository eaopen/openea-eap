// 应用全局配置
module.exports = {
  // baseUrl: 'http://api-dashboard.eap.iocoder.cn',
  baseUrl: 'http://localhost:48080',
  baseApi: '/admin-api',
  // 应用信息
  appInfo: {
    // 应用名称
    name: "eap-app",
    // 应用版本
    version: "1.0.0",
    // 应用logo
    logo: "/static/logo.png",
    // 官方网站
    site_url: "https://iocoder.cn",
    // 政策协议
    agreements: [{
        title: "隐私政策",
        url: "https://iocoder.cn"
      },
      {
        title: "用户服务协议",
        url: "https://iocoder.cn"
      }
    ]
  }
}
