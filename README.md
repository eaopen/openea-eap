# Open EAP

## 概述
开放企业应用平台，整合各种开源项目作为企业应用快速开发的基础框架。

## solution解决方案

### 基础版 eap-fast

前后端分离的企业应用管理后台

```
cd eap-fast
docker-compose up -d 
```
运行前需要执行创建image脚本
```
# eap-fast
sh ./service/eap-fast/buid-eap-fast-image.sh

# eap-admin-vue

sh ./ui/eap-admin-vue/buid-eap-admin-vue-image.sh
```

### 微服务版 eap-cloud

基础版增加微服务框架以及OAuth2认证

* oauth  认证服务(OAuth2+JWT)

* gateway 服务网关(spring cloud gateway)


### bpm集成版 eap-bpm

在微服务版之上集成工作流相关内容



# 企业应用平台主要模块

## service - eap后端服务
企业应用基础平台后端部分

* eap-fast 企业应用管理Admin

组织人员角色

菜单及权限

## ui - eap前端UI

企业应用接触平台的前端部分，默认选择eap-admin-vue

* eap-admin-vue 

VUE版本的eap-fast前端功能(基于renren-fast-vue)

* eap-admin-iview

基于iview-admin改进版本（未完成）

* eap-admin-element

基于vue-element-admin改进版本(未完成)

## support - 支持内容 

* eap-generator 代码生成工具


# 参考项目 refer

参考众多开源项目实现，一并表示感谢，部分项目列出如下：

* renren-fast

基于vue、element-ui构建开发，实现renren-fast后台管理前端功能，提供一套更优的前端解决方案。

https://github.com/renrenio/renren-fast-vue

https://github.com/renrenio/renren-security

* jeecg 

基于代码生成器的智能开发平台！采用前后端分离架构：SpringBoot 2.x，Mybatis，Shiro，JWT，Vue&Ant Design

https://github.com/zhangdaiscott/jeecg-boot

* eladmin

基于 Spring Boot 2.1.0 、 Jpa、 Spring Security、redis、Vue的前后端分离的后台管理系统

https://github.com/elunez/eladmin

* jeeSpringCloud

https://gitee.com/JeeHuangBingGui/jeeSpringCloud


