# Open BPM 
## 概述
基于开源工作流引擎的BPM套装，整合各种开源项目或组件，实现企业应用快速集成工作流能力。


# OpenBPM 应用方案
## solution 解决方案
迁移到bpm-quickstart和eap相关的项目

# OpenBPM 服务接口

## starter
* 基于spring-boot封装bpm-starter
* spring自动配置(base/security/sys/wf)

## bpm-rest
基于bpm-start封装bpm-rest应用

***bpm-rest***为OpenBPM后端服务应用，可作为工作流平台的后台基础版本。

## bpm-allinone

快速demo展示（需要安装docker和docker-compose)
```
cd openbpm/bpm-allinone
docker-compose up -d
```
* web端访问地址 http://localhost:7080

* mobile端访问地址 http://localhost:7082

* 后台服务API http://localhost:7080/api/swagger-ui.hmtl

用户名和密码为admin/admin


# OpenBPM 平台模块

## bpm-common 通用功能
* base 通用技术组件，包含api,core, rest, db。

## bpm-core 核心功能
bpm包含业务对象bus,表单form和流程wf
* bus 业务对象模块
* form 表单模块
* wf 流程模块

## bpm-support 支持功能
support包含base, auth,org以及sys
* org 内建组织权限功能
* sys 内建后台基础服务功能

## bpm-ui 前端UI相关功能
ui包含web前端explorer-ng和web打包工程以及mobile-vue 移动端APP

* explorer BPM Web端UI

基于VUE和angular(activiti model采用angular)

* mobile BPM移动端UI

基于VUE开发


# 开发及调试

## bpm-dev 开发调试

启动bpm-rest服务

```
mvn clean install
cd openbpm/bpm-rest
mvn spring-boot:run
```
查看BPM Rest提供服务API

http://localhost:8080/api/swagger-ui.html

前端开发调试
```
cd bpm-ui/explorer-ng
## 需要后端服务地址为 http://localhost:8080/
npm install
npm run dev
```
http://localhost:8001/


# 参考项目 refer
参考众多开源项目实现，一并表示感谢，部分项目列出如下：
* activiti 开源工作流引擎
https://github.com/Activiti/Activiti

* spring boot 后端基础框架
https://github.com/spring-projects/spring-boot

* jeeSpringCloud 企业开发平台(包含基于Activiti工作流)
https://gitee.com/JeeHuangBingGui/jeeSpringCloud

* agile bpm 企业工作流管理(基于Activiti)
https://github.com/AgileBPM/agile-bpm-basic

* sz BPM 企业工作流管理(基于Activiti)