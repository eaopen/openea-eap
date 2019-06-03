#!/usr/bin/env bash
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
cd ${DIR}
mvn clean  -Dmaven.test.skip=true
mvn package  -Dmaven.test.skip=true
cd target
artifactId=$(ls eap*.jar |awk -F '[-]' '{print $1"-"$2}')
version=$(ls eap*.jar |awk -F '[-]' '{print $3}')
cd ..
docker build --build-arg JAR_FILE=target/*.jar --build-arg PORT=${port} -t ${artifactId}:latest -f ./Dockerfile .
docker save -o ./target/${artifactId}-latest-image.tar ${artifactId}:latest
echo "${artifactId}:latest镜像构建成功,已导出到target目录"