#认证中心
实现了oauth2+jwt协议，token是按照jwt规范生成的，且私钥签名。
任何客户端通都过公钥验证token是否是伪造的。

##oauth协议流程

    +--------+                               +---------------+
    |        |--(A)- Authorization Request ->|   Resource    |
    |        |                               |     Owner     |
    |        |<-(B)-- Authorization Grant ---|               |
    |        |                               +---------------+
    |        |
    |        |                               +---------------+
    |        |--(C)-- Authorization Grant -->| Authorization |
    | Client |                               |     Server    |
    |        |<-(D)----- Access Token -------|               |
    |        |                               +---------------+
    |        |
    |        |                               +---------------+
    |        |--(E)----- Access Token ------>|    Resource   |
    |        |                               |     Server    |
    |        |<-(F)--- Protected Resource ---|               |
    +--------+                               +---------------+
    
在此不再细讲，具体的请参考oauth协议原文
https://tools.ietf.org/html/rfc6749#section-1.3.1
    
##接口   
请调用者务必理解oauth和jwt协议,以及BearerToken的传参方式。
  
oauth2 api

| 接口 | 用途   |
| ------  | ------ |
| /oauth/authorize | 请求授权  |
| /oauth/token | 获取access_token |

在oauth2的基础上，本项目又实现了如下api

| 接口 | 用途   |
| ------  | ------ |
| /oauth/public_key | 获取公钥  |
| /oauth/logout | 单点退出并使accesstoken失效。此接口不要用ajax方式，必须使用浏览器跳转方式，否则无法清除session的cookie |
  

##技术概览

| 框架 | 用途 | 版本 | 官网 |
| ------ | ------ | ------ | ------ |
| spring-boot | 主框架 | 2.0.8.RELEASE | http://spring.io/projects/spring-boot |
| spring-cloud | 主框架 | Finchley.SR2 | http://spring.io/projects/spring-cloud |
| spring-security | 安全控制 | 按默认 | https://spring.io/projects/spring-security |
| openfeign | 简化服务调用 | 按默认 | https://spring.io/projects/spring-security |
| spring-boot-starter-thymeleaf,thymeleaf | 集成thymeleaf，页面模板 | 按默认 | https://www.thymeleaf.org |
| ojdbc6 | JDBC驱动 | 11.2.0.3 | https://www.oracle.com/technetwork/database/application-development/jdbc/downloads/index.html |
| HikariCP | 数据库连接池 | 2.7.9 | http://brettwooldridge.github.io/HikariCP/ |
| lombok | 简化代码 | 1.16.22 | https://www.projectlombok.org |
| spring-boot-starter-data-jpa | 数据层框架 | 按默认 | https://spring.io/projects/spring-data-jpa |
| spring-boot-starter-actuator | 健康监控,springboot的一部分 | 按默认 | https://docs.spring.io/spring-boot/docs/2.0.8.RELEASE/reference/htmlsingle/#production-ready |
| spring-session-data-redis | 集成spring session，并以redis作为存储层 | 按默认 | https://spring.io/projects/spring-session#overview |
| redis | 缓存 | 5.0.3 | https://redis.io |
| spring-cloud-starter-oauth2 | 集成oauth2 | 按默认 | https://spring.io/projects/spring-security-oauth#overview |
| spring-cloud-starter-consul | 集成consul | 按默认 | http://spring.io/projects/spring-cloud-consul |
| spring-cloud-starter-openfeign | 集成openfeign | 按默认 | http://cloud.spring.io/spring-cloud-openfeign/single/spring-cloud-openfeign.html |


##相关协议
oauth

https://oauth.net/2/
https://tools.ietf.org/html/rfc6749#section-1.3.1

jwt

https://jwt.io

OAuth 2.0 Bearer Token

https://tools.ietf.org/html/rfc6750

##登录页面
位置
/oauth/src/main/resources/views/login.html

## 准备
### JDK

### git
从 https://git-scm.com 下载git安装包并安装之。

### maven
从 https://maven.apache.org下载最新版本的maven，并解压到任意位置。为了方便，请将maven的bin文件夹路径添加环境变量path中。
环境变量请参考https://jingyan.baidu.com/article/acf728fd68b4bef8e510a31c.html

在 用户目录/.m2下创建settings.xml，其内容如下
```xml
<?xml version="1.0" encoding="UTF-8"?>
<settings xmlns="http://maven.apache.org/SETTINGS/1.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0 http://maven.apache.org/xsd/settings-1.0.0.xsd">
  <mirrors>
    <mirror>
      <id>alimaven</id>
      <name>aliyun maven</name>
      <url>http://maven.aliyun.com/nexus/content/groups/public/</url>
      <mirrorOf>central</mirrorOf>
    </mirror>
  </mirrors> 
</settings>
```

### sts4 
从 https://spring.io/tools 下载适合您操作系统的版本

### clone代码并导入到sts
git clone https://xxxxxxxxx

打开sts，点开菜单File>Import，在弹出的Import对话框中选择已存在的maven项目。点"下一步"按钮。选择项目所在位置。点击"完成"按钮

开发工具会自动下载mavne依赖

### lombok
lombok的功能需要开发工具安装lombok插件
请参考https://www.projectlombok.org/setup/eclipse

## 开发调试
首先找到/etech-engine/src/main/java/com/sec/etech文件夹下面的XXXApplication类，右键菜单Debug As> Spring Boot App。

## 打包
在项目根目录，打开命令行，输入命令
```bash
mvn package -Dmaven.test.skip=true
```

## 调用
GET http://localhost:8081/oauth/authorize?response_type=token&client_id=XXX_ADMIN&redirect_uri=http://www.baidu.com

