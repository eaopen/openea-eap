## JDK 选择

项目默认选择OpenJDK8, 可兼容OpenJDK11

### jdk8

### jdk11
从 http://jdk.java.net/11/ 下载适合你的操作系统的安装包并安装之。
安装之后还要设置环境变量，关于环境变量网上资料很多，请参考http://www.runoob.com/w3cnote/windows10-java-setup.html，或者自行按关键字"java环境变量"百度。

需要强调的是jdk11移除了部分模块，影响了本项目的功能。本项目中的pom.xml文件需要有以下依赖，不过您无需重复添加。
```xml
<dependencys>
  <dependency>
      <groupId>org.glassfish.jaxb</groupId>
      <artifactId>jaxb-runtime</artifactId>
      <version>2.3.0.1</version>
  </dependency>
  <dependency>
      <groupId>org.javassist</groupId>
      <artifactId>javassist</artifactId>
      <version>3.23.1-GA</version>
  </dependency>
</dependencys>
```