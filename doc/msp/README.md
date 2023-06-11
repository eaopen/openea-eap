#  microservices-platform

基于以下开源项目改造：
- [microservices-platform](https://gitee.com/zlt2000/microservices-platform)
- [open-capacity-platform](https://gitee.com/owenwangwen/open-capacity-platform)
- [open-cloud/open-cloud-pro](https://github.com/liuyadu/open-cloud)

## 1. 项目介绍

* 前后端分离的企业级微服务架构
* 主要针对解决微服务和业务开发时常见的**非功能性需求**
* 深度定制`Spring Security`真正实现了基于`RBAC`、`jwt`和`oauth2`的无状态统一权限认证的解决方案
* 提供应用管理，方便第三方系统接入，**支持多租户(应用隔离)**
* 引入组件化的思想实现高内聚低耦合并且高度可配置化
* 注重代码规范，严格控制包依赖，每个工程基本都是最小依赖

### 快速启动

必要启动模块
- 启动认证中心uaa
    - 运行 UaaServerApp
- 启动用户中心bussiness/user-center
    - 运行 UserCenterApp
- 启动网关gateway/sc-gateway
    - 运行 sc-gateway的org.openea.SCGatewayApp
- 启动前端工程web/back-web
    - 运行 BackWebApplication
      http://127.0.0.1:8066

- Demo账号
    * 账号密码：admin/admin
    * 配置中心：nacos/nacos
    * APM监控账号密码：admin/admin
    * Grafana账号：zlt/zlt123
    * txlcn事务管理器密码：admin
    * 任务管理账号密码：admin/123456
    * Sentinel：sentinel/sentinel

- 单点登录demo (demo/sso-demo)
    * **ss-sso**：使用springSecurity来实现自动单点登录，非前后端分离
    * **web-sso**：前后端分离的单点登录

## 2. 项目总体架构图
![mark](doc/img/zlt-arch.jpg)

&nbsp;

## 3. 功能介绍
![mark](doc/img/zlt-func.jpg)

&nbsp;

## 4. 模块说明

```lua
ea-msp(MicroService Platform) -- 父项目，公共依赖
│  ├─business -- 业务模块一级工程
│  │  ├─user-center -- 用户中心[7000]
│  │  ├─file-center -- 文件中心[5000]
│  │  ├─code-generator -- 代码生成器[7300]
│  │  ├─search-center -- 搜索中心
│  │  │  ├─search-client -- 搜索中心客户端
│  │  │  ├─search-server -- 搜索中心服务端[7100]
│  │─commons -- 通用工具一级工程
│  │  ├─auth-client-spring-boot-starter -- 封装spring security client端的通用操作逻辑
│  │  ├─common-core -- 封装通用操作逻辑
│  │  ├─common-spring-boot-starter -- 封装通用操作逻辑
│  │  ├─db-spring-boot-starter -- 封装数据库通用操作逻辑
│  │  ├─log-spring-boot-starter -- 封装log通用操作逻辑
│  │  ├─redis-spring-boot-starter -- 封装Redis通用操作逻辑
│  │  ├─ribbon-spring-boot-starter -- 封装Ribbon和Feign的通用操作逻辑
│  │  ├─sentinel-spring-boot-starter -- 封装Sentinel的通用操作逻辑
│  │  ├─swagger2-spring-boot-starter -- 封装Swagger通用操作逻辑
│  ├─config -- 配置中心
│  ├─doc -- 项目文档
│  ├─gateway -- api网关一级工程
│  │  ├─sc-gateway -- spring-cloud-gateway[9900]
│  │  ├─zuul-gateway -- netflix-zuul[9900]
│  ├─job -- 分布式任务调度一级工程
│  │  ├─job-admin -- 任务管理器[8081]
│  │  ├─job-core -- 任务调度核心代码
│  │  ├─job-executor-samples -- 任务执行者executor样例[8082]
│  ├─monitor -- 监控一级工程
│  │  ├─sc-admin -- 应用监控[6500]
│  │  ├─log-center -- 日志中心[6200]
│  ├─uaa -- spring-security认证中心[8000]
│  ├─register -- 注册中心Nacos[8848]
│  ├─web -- 前端一级工程
│  │  ├─back-web -- 后台前端[8066]
│  ├─transaction -- 事务一级工程
│  │  ├─txlcn-tm -- tx-lcn事务管理器[7970]
│  ├─demo -- demo一级工程
│  │  ├─txlcn-demo -- txlcn分布式事务demo
│  │  ├─seata-demo -- seata分布式事务demo
│  │  ├─sharding-jdbc-demo -- sharding-jdbc分库分表demo
│  │  ├─rocketmq-demo -- rocketmq和mq事务demo
│  │  ├─sso-demo -- 单点登录demo
```

5. 相关知识

- ZLT微服务框架文档  
  https://www.kancloud.cn/zlt2000/microservices-platform/919414
