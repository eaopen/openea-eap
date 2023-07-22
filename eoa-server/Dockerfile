## AdoptOpenJDK 停止发布 OpenJDK 二进制，而 Eclipse Temurin 是它的延伸，提供更好的稳定性
## 感谢复旦核博士的建议！灰子哥，牛皮！
FROM eclipse-temurin:8-jre as builder

## 创建目录，并使用它作为工作目录
RUN mkdir -p /eoa-server
WORKDIR /eoa-server
## 将后端项目的 Jar 文件，复制到镜像中
COPY ./target/eoa-server.jar app.jar
RUN java -Djarmode=layertools -jar app.jar extract

FROM eclipse-temurin:8-jre

## 设置 TZ 时区
ENV TZ=Asia/Shanghai
## 设置 JAVA_OPTS 环境变量，可通过 docker run -e "JAVA_OPTS=" 进行覆盖
ENV JAVA_OPTS="-Xms512m -Xmx2048m -Djava.security.egd=file:/dev/./urandom"

WORKDIR /eoa-server
COPY --from=builder eoa-server/dependencies/ ./
COPY --from=builder eoa-server/spring-boot-loader/ ./
COPY --from=builder eoa-server/internal-dependencies/ ./
COPY --from=builder eoa-server/snapshot-dependencies/ ./
COPY --from=builder eoa-server/application/ ./

## 应用参数
ENV ARGS=""

## 暴露后端项目的 48080 端口
EXPOSE 48080

## 启动后端项目
#CMD java ${JAVA_OPTS} -jar app.jar $ARGS
ENTRYPOINT ["java", "org.springframework.boot.loader.JarLauncher","$ARGS"]
