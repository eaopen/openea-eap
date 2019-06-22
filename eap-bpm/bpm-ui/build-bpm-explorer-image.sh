#!/usr/bin/env bash
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
cd ${DIR}

if [ ! -d "explorer" ];then
  mkdir explorer
fi

# copy bpm-ui/explorer source code
bpm_explorer_path=../../../bpm/bpm-ui/explorer
cp -R ${bpm_explorer_path} explorer
# update config and new file
cp -Rf bpm-explorer/** explorer/

# docker运行npm
cd explorer
sh ../tool/npm-run-build.sh

artifactId=eap-bpm-ui
version=1.0.0


# docker
cd ..
docker build -t ${artifactId}:${version} -f ./explorer_Dockerfile .
docker tag  ${artifactId}:${version}  ${artifactId}:latest

# image导出
if [ ! -d "target" ];then
  mkdir target
fi
#docker save -o ./target/${artifactId}-${version}-image.tar ${artifactId}:${version}
#echo "${artifactId}:${version}镜像构建成功,已导出到target目录"
#docker save -o ./target/${artifactId}-lasest-image.tar ${artifactId}:latest
#echo "${artifactId}:latest镜像构建成功,已导出到target目录"

# hub.docker.com/szopen
group=szopen
#docker login -u szopen --password
docker tag ${artifactId}:${version} ${group}/${artifactId}:${version}
#docker push ${group}/${artifactId}:${version}
docker tag ${artifactId}:${version} ${group}/${artifactId}:latest
#docker push ${group}/${artifactId}:latest
