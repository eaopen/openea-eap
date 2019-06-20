#!/usr/bin/env bash
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
cd ${DIR}
# mvn
mvn clean -DskipTests
mvn package -DskipTests

cd target
#artifactId=$(ls bpm*.jar |awk -F '[-]' '{print $1"-"$2}')
#version=$(ls bpm*.jar |awk -F '[-]' '{print $3}')
artifactId=$(sed '/artifactId/!d;s/.*=//' maven-archiver/pom.properties)
version=$(sed '/version/!d;s/.*=//' maven-archiver/pom.properties)
#version=latest

# unpack
cd ..
mkdir -p target/dependency
(cd target/dependency; jar -xf ../*.jar)

# docker
docker build -t ${artifactId}:${version} -f ./Dockerfile .
docker tag  ${artifactId}:${version}  ${artifactId}:latest

# image导出
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