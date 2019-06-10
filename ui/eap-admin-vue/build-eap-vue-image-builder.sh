#!/usr/bin/env bash
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
cd ${DIR}
docker build -t eap-admin-vue:latest  -f ./Dockerfile_builder .
if [ ! -d "target" ];then
  mkdir target
fi

docker save -o ./target/eap-admin-vue-latest-image.tar eap-admin-vue:latest
echo "eap-admin-vue:latest镜像构建成功,已导出到target目录"
