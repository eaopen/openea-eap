FROM node:10.15.1-alpine
MAINTAINER openbea
#移动当前目录下面的文件到app目录下
ADD ./dist /app/
#进入到app目录下面，类似cd
WORKDIR /app
#安装依赖
RUN npm config set unsafe-perm true
RUN npm install -g serve
#对外暴露的端口
EXPOSE 8001
#程序启动脚本
ENTRYPOINT ["serve","-s", "."]
