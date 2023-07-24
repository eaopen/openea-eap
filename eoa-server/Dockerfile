FROM eclipse-temurin:8 as builder

WORKDIR application

ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} application.jar
RUN java -Djarmode=layertools -jar application.jar extract

FROM eclipse-temurin:8

## 设置 TZ 时区
ENV TZ=Asia/Shanghai
## 设置 JAVA_OPTS 环境变量，可通过 docker run -e "JAVA_OPTS=" 进行覆盖
ENV JAVA_OPTS="-Xms1024m -Xmx4096m -Djava.security.egd=file:/dev/./urandom"

WORKDIR application
COPY --from=builder application/dependencies/ ./
COPY --from=builder application/spring-boot-loader/ ./
#COPY --from=builder application/internal-dependencies/ ./
COPY --from=builder application/snapshot-dependencies/ ./
COPY --from=builder application/application/ ./

## 应用参数
ENV ARGS=""

## 暴露后端项目的 48080 端口
EXPOSE 48080

## 启动后端项目
#CMD java ${JAVA_OPTS} -jar app.jar $ARGS
#ENTRYPOINT ["java", "org.springframework.boot.loader.JarLauncher","$ARGS"]
CMD java ${JAVA_OPTS} org.springframework.boot.loader.JarLauncher $ARGS
