# Open EAP
This project is licensed under the terms of the MIT license.
## 概述 
开放企业应用平台，整合各种开源项目作为企业应用快速开发的基础框架，支持i8n国际化。

本次改版以芋道开源 YunaiV/ruoyi-vue-pro (MIT许可)为基础改版，整合OBPM、MSP、Jeecg等开源项目的部分模块。
OpenEAP前后端分离，采用后端Spring boot 2.x单体多模块，前端vue2+element-UI 主流路线，预留无缝升级机制。
单体服务无缝切换为微服务（部分完成）。


## 🐯 平台简介

![架构图](https://raw.githubusercontent.com/eaopen/openea-eap/dev/doc/yudao/ruoyi-vue-pro-architecture.png)

* 管理后台的电脑端：Vue2 提供 [element-ui] eap-ui-admin 版本
* 管理后台的移动端：采用 [uni-app](https://github.com/dcloudio/uni-app) 方案，一份代码多终端适配，同时支持 APP、小程序、H5！
* 后端采用 Spring Boot 多模块架构、MySQL + MyBatis Plus、Redis + Redisson
* 数据库可使用 MySQL、Oracle、PostgreSQL、SQL Server、MariaDB、TiDB 等
* 权限认证使用 Spring Security & Token & Redis，支持多终端、多种用户的认证系统，支持 SSO 单点登录
* 支持加载动态权限菜单，按钮级别权限控制，本地缓存提升性能
* 工作流使用 Activiti/Flowable，支持动态表单、在线设计流程、会签 / 或签、多种任务分配方式
* 高效率开发，使用代码生成器可以一键生成前后端代码 + 单元测试 + Swagger 接口文档 + Validator 参数校验
* 集成阿里云、腾讯云等短信渠道，集成 MinIO、阿里云、腾讯云、七牛云等云存储服务
* 集成报表设计器、大屏设计器，通过拖拽即可生成酷炫的报表与大屏

## 🐼 内置功能

系统内置多种多种业务功能，可以用于快速你的业务系统：

![功能分层](https://raw.githubusercontent.com/eaopen/openea-eap/dev/doc/yudao/ruoyi-vue-pro-biz.png)

* 系统功能
* 基础设施
* 工作流程

### 系统功能

|     | 功能    | 描述                              |
|-----|-------|---------------------------------|
|     | 用户管理  | 用户是系统操作者，该功能主要完成系统用户配置          |
| ⭐️  | 在线用户  | 当前系统中活跃用户状态监控，支持手动踢下线           |
|     | 角色管理  | 角色菜单权限分配、设置角色按机构进行数据范围权限划分      |
|     | 菜单管理  | 配置系统菜单、操作权限、按钮权限标识等，本地缓存提供性能    |
|     | 部门管理  | 配置系统组织机构（公司、部门、小组），树结构展现支持数据权限  |
|     | 岗位管理  | 配置系统用户所属担任职务                    |
| 🚀  | 国际化管理 | 国际化语言和词条翻译维护                    |
|     | 国际化工具 | 前后端词条抽取工具，自动翻译集成                |
| 🚀  | 租户管理  | 配置系统租户，支持 SaaS 场景下的多租户功能        |
| 🚀  | 租户套餐  | 配置租户套餐，自定每个租户的菜单、操作、按钮的权限       |
|     | 字典管理  | 对系统中经常使用的一些较为固定的数据进行维护          |
| 🚀  | 短信管理  | 短信渠道、短息模板、短信日志，对接阿里云、腾讯云等主流短信平台 |
| 🚀  | 邮件管理  | 邮箱账号、邮件模版、邮件发送日志，支持所有邮件平台       |
| 🚀  | 站内信   | 系统内的消息通知，提供站内信模版、站内信消息          |
| 🚀  | 操作日志  | 系统正常操作日志记录和查询，集成 Swagger 生成日志内容 |
| ⭐️  | 登录日志  | 系统登录日志记录查询，包含登录异常               |
| 🚀  | 错误码管理 | 系统所有错误码的管理，可在线修改错误提示，无需重启服务     |
|     | 通知公告  | 系统通知公告信息发布维护                    |
| 🚀  | 敏感词   | 配置系统敏感词，支持标签分组                  |
| 🚀  | 应用管理  | 管理 SSO 单点登录的应用，支持多种 OAuth2 授权方式 |
| 🚀  | 地区管理  | 展示省份、城市、区镇等城市信息，支持 IP 对应城市      |


### 基础设施

|     | 功能       | 描述                                           |
|-----|----------|----------------------------------------------|
| 🚀  | 代码生成     | 前后端代码的生成（Java、Vue、SQL、单元测试），支持 CRUD 下载       |
| 🚀  | 系统接口     | 基于 Swagger 自动生成相关的 RESTful API 接口文档          |
| 🚀  | 数据库文档    | 基于 Screw 自动生成数据库文档，支持导出 Word、HTML、MD 格式      |
|     | 表单构建     | 拖动表单元素生成相应的 HTML 代码，支持导出 JSON、Vue 文件         |
| 🚀  | 配置管理     | 对系统动态配置常用参数，支持 SpringBoot 加载                 |
| ⭐️  | 定时任务     | 在线（添加、修改、删除)任务调度包含执行结果日志                     |
| 🚀  | 文件服务     | 支持将文件存储到 S3（MinIO、阿里云、腾讯云、七牛云）、本地、FTP、数据库等   | 
| 🚀  | API 日志   | 包括 RESTful API 访问日志、异常日志两部分，方便排查 API 相关的问题   |
|     | MySQL 监控 | 监视当前系统数据库连接池状态，可进行分析SQL找出系统性能瓶颈              |
|     | Redis 监控 | 监控 Redis 数据库的使用情况，使用的 Redis Key 管理           |
| 🚀  | 消息队列     | 基于 Redis 实现消息队列，Stream 提供集群消费，Pub/Sub 提供广播消费 |
| 🚀  | Java 监控  | 基于 Spring Boot Admin 实现 Java 应用的监控           |
| 🚀  | 链路追踪     | 接入 SkyWalking 组件，实现链路追踪                      |
| 🚀  | 日志中心     | 接入 SkyWalking 组件，实现日志中心                      |
| 🚀  | 分布式锁     | 基于 Redis 实现分布式锁，满足并发场景                       |
| 🚀  | 幂等组件     | 基于 Redis 实现幂等组件，解决重复请求问题                     |
| 🚀  | 服务保障     | 基于 Resilience4j 实现服务的稳定性，包括限流、熔断等功能          |
| 🚀  | 日志服务     | 轻量级日志中心，查看远程服务器的日志                           |
| 🚀  | 单元测试     | 基于 JUnit + Mockito 实现单元测试，保证功能的正确性、代码的质量等    |


## 🐨 技术栈

### 模块

| 项目                  | 说明                          |
|---------------------|-----------------------------|
| `eap-dependencies`  | Maven 依赖版本管理                |
| `eap-framework`     | Java 框架拓展                   |
| `eap-module-system` | 系统功能的 Module 模块, 包含boot和cloud |
| `eap-module-member` | 会员中心的 Module 模块             |
| `eap-module-infra`  | 基础设施的 Module 模块, 包含boot和cloud |
| `eap-module-obpm`   | 集成基于工作流的低代码平台               |
| `eap-module-report` | 大屏报表 Module 模块              |
| `eap-server`        | 管理后台 + 用户 APP 的服务端          |
| `eoa-server`        | OA + 管理后台 + 用户 APP 的服务端     |
| `eap-cloud`         | 微服务基础平台，包含网关APISIX/追踪Skywalking |

### 框架

| 框架                                                                                          | 说明            | 版本          | 学习指南                                                           |
|---------------------------------------------------------------------------------------------|---------------|-------------|----------------------------------------------------------------|
| [Spring Boot](https://spring.io/projects/spring-boot)                                       | 应用开发框架        | 2.7.12      | [文档](https://github.com/YunaiV/SpringBoot-Labs)                |
| [MySQL](https://www.mysql.com/cn/)                                                          | 数据库服务器        | 5.7 / 8.0+  |                                                                |
| [Druid](https://github.com/alibaba/druid)                                                   | JDBC 连接池、监控组件 | 1.2.16      | [文档](http://www.iocoder.cn/Spring-Boot/datasource-pool/?eap) |
| [MyBatis Plus](https://mp.baomidou.com/)                                                    | MyBatis 增强工具包 | 3.5.3.1     | [文档](http://www.iocoder.cn/Spring-Boot/MyBatis/?eap)         |
| [Dynamic Datasource](https://dynamic-datasource.com/)                                       | 动态数据源         | 3.6.1       | [文档](http://www.iocoder.cn/Spring-Boot/datasource-pool/?eap) |
| [Redis](https://redis.io/)                                                                  | key-value 数据库 | 5.0 / 6.0   |                                                                |
| [Redisson](https://github.com/redisson/redisson)                                            | Redis 客户端     | 3.18.0      | [文档](http://www.iocoder.cn/Spring-Boot/Redis/?eap)           |
| [Spring MVC](https://github.com/spring-projects/spring-framework/tree/master/spring-webmvc) | MVC 框架        | 5.3.24      | [文档](http://www.iocoder.cn/SpringMVC/MVC/?eap)               |
| [Spring Security](https://github.com/spring-projects/spring-security)                       | Spring 安全框架   | 5.7.6       | [文档](http://www.iocoder.cn/Spring-Boot/Spring-Security/?eap) |
| [Hibernate Validator](https://github.com/hibernate/hibernate-validator)                     | 参数校验组件        | 6.2.5       | [文档](http://www.iocoder.cn/Spring-Boot/Validation/?eap)      |
| [Flowable](https://github.com/flowable/flowable-engine)                                     | 工作流引擎         | 6.8.0       | [文档](https://doc.iocoder.cn/bpm/)                              |
| [Quartz](https://github.com/quartz-scheduler)                                               | 任务调度组件        | 2.3.2       | [文档](http://www.iocoder.cn/Spring-Boot/Job/?eap)             |
| [Springdoc](https://springdoc.org/)                                                         | Swagger 文档    | 1.6.15      | [文档](http://www.iocoder.cn/Spring-Boot/Swagger/?eap)         |
| [Resilience4j](https://github.com/resilience4j/resilience4j)                                | 服务保障组件        | 1.7.1       | [文档](http://www.iocoder.cn/Spring-Boot/Resilience4j/?eap)    |
| [SkyWalking](https://skywalking.apache.org/)                                                | 分布式应用追踪系统     | 8.12.0      | [文档](http://www.iocoder.cn/Spring-Boot/SkyWalking/?eap)      |
| [Spring Boot Admin](https://github.com/codecentric/spring-boot-admin)                       | Spring Boot 监控平台 | 2.7.10      | [文档](http://www.iocoder.cn/Spring-Boot/Admin/?eap)           |
| [Jackson](https://github.com/FasterXML/jackson)                                             | JSON 工具库      | 2.13.3      |                                                                |
| [MapStruct](https://mapstruct.org/)                                                         | Java Bean 转换  | 1.5.5.Final | [文档](http://www.iocoder.cn/Spring-Boot/MapStruct/?eap)       |
| [Lombok](https://projectlombok.org/)                                                        | 消除冗长的 Java 代码 | 1.18.26     | [文档](http://www.iocoder.cn/Spring-Boot/Lombok/?eap)          |
| [JUnit](https://junit.org/junit5/)                                                          | Java 单元测试框架   | 5.8.2       | -                                                              |
| [Mockito](https://github.com/mockito/mockito)                                               | Java Mock 框架  | 4.8.0       | -                                                              |
| [APISIX](https://github.com/apache/apisix)                                                  | API Gateway   | 3.2.1       | -                                                              |
| [Skywalking](https://github.com/apache/skywalking)                                          | APM 日志追踪      | 9.5.0       | -                                                              |

# 参考
## 参考项目 refer

参考众多开源项目实现，一并表示感谢，部分项目列出如下：

* yudao/ruoyi-vue-pro

RuoYi-Vue 全新 Pro 版本，优化重构所有功能。   
基于 Spring Boot + MyBatis Plus + Vue & Element 实现的后台管理系统 + 微信小程序，支持 RBAC 动态权限、数据权限、SaaS 多租户、Flowable 工作流、三方登录、支付、短信、商城等功能

https://github.com/YunaiV/ruoyi-vue-pro

* yudao/yudao-cloud   

Ruoyi-vue-pro 全新 Cloud 版本，优化重构所有功能。  
基于 Spring Cloud Alibaba + MyBatis Plus + Vue & Element 实现的后台管理系统 + 用户小程序，支持 RBAC 动态权限、多租户、数据权限、工作流、三方登录、支付、短信、商城等功能。

https://github.com/YunaiV/yudao-cloud

* jeecg

基于代码生成器的智能开发平台！采用前后端分离架构：SpringBoot 2.x，Mybatis，Shiro，JWT，Vue&Ant Design

https://github.com/zhangdaiscott/jeecg-boot

https://help.jeecg.com/


* eladmin

基于 Spring Boot 2.1.0 、 Jpa、 Spring Security、redis、Vue的前后端分离的后台管理系统

https://github.com/elunez/eladmin

* jeeSpringCloud

https://gitee.com/JeeHuangBingGui/jeeSpringCloud

* renren-fast

基于vue、element-ui构建开发，实现renren-fast后台管理前端功能，提供一套更优的前端解决方案。

https://github.com/renrenio/renren-fast-vue

https://github.com/renrenio/renren-security

* JNPF开发平台

官方文档，非开源  
https://service.jnpfsoft.com/index/index/operate.html?cid=331&product_id=89

## 🐶 参考--芋道开源新手必读
* 演示地址【Vue2 + element-ui】：<http://dashboard.eap.iocoder.cn>
* 启动文档：<https://doc.iocoder.cn/quick-start/>
* 视频教程：<https://doc.iocoder.cn/video/>