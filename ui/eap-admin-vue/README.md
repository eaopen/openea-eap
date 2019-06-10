## eap-admin-vue
- eap-admin-vue基于vue、element-ui构建开发，实现eap-fast后台管理前端功能，提供一套更优的前端解决方案
- 前后端分离，通过token进行数据交互，可独立部署
- 主题定制，通过scss变量统一一站式定制
- 动态菜单，通过菜单管理统一管理访问路由
- 数据切换，通过mock配置对接口数据／mock模拟数据进行切换
- 发布时，可动态配置CDN静态资源／切换新旧版本


## 说明文档

### 开发
```bash
# 克隆项目
git clone https://github.com/openea-eap.git
cd ./ui/eap-admin-vue

# 安装依赖
npm install

# 启动服务
npm run dev
```

启动完成后会自动打开浏览器访问 http://localhost:8001

### 打包

```bash
npm run build
```

## 常见问题

~~~~
Q: 开发时，如何连接后台项目api接口？
A: 修改/static/config/index.js目录文件中 window.SITE_CONFIG['baseUrl'] = '本地api接口请求地址';
 
Q: 开发时，如何解决跨域？
A: 
1)修改/config/dev.env.js目录文件中OPEN_PROXY: true开启代理
2)修改/config/index.js目录文件中proxyTable对象target: '代理api接口请求地址'
3)重启本地服务

Q: 开发时，如何提前配置CDN静态资源？
A: 修改/static/config/index-[qa/uat/prod].js目录文件中window.SITE_CONFIG['domain'] = '静态资源cdn地址';
 
Q: 构建生成后，发布需要上传哪些文件？
A: /dist目录下：1805021549（静态资源，18年05月03日15时49分）、config（配置文件）、index.html
 
Q: 构建生成后，如何动态配置CDN静态资源？
A: 修改/dist/config/index.js目录文件中window.SITE_CONFIG['domain'] = '静态资源cdn地址';

Q: 构建生成后，如何动态切换新旧版本？
A: 修改/dist/config/index.js目录文件中 window.SITE_CONFIG['version'] = '旧版本号';
~~~~

## 参考

eap-admin-vue最初版本来自于 [renren-fast-vue](https://github.com/renrenio/renren-fast-vue)

配置文档
https://github.com/renrenio/renren-fast-vue/wiki
