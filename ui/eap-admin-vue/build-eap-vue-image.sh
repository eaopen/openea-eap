#!/usr/bin/env bash
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
cd ${DIR}
# test on node 10 and node 12 is not work
node -v
npm config set unsafe-perm true
npm config set registry https://registry.npm.taobao.org
npm install node-sass@latest
npm install
#npm audit fix
npm run build

docker build -t eap-admin-vue:latest  -f ./Dockerfile .
if [ ! -d "target" ];then
  mkdir target
fi

docker save -o ./target/eap-admin-vue-latest-image.tar eap-admin-vue:latest
echo "eap-admin-vue:latest镜像构建成功,已导出到target目录"
