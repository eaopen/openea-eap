#!/usr/bin/env bash
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
cd ${DIR}
mvn clean  -Dmaven.test.skip=true
mvn package  -Dmaven.test.skip=true

cd target
#artifactId=$(ls eap*.jar |awk -F '[-]' '{print $1"-"$2}')
#version=$(ls eap*.jar |awk -F '[-]' '{print $3}')
artifactId=$(sed '/artifactId/!d;s/.*=//' maven-archiver/pom.properties)
version=$(sed '/version/!d;s/.*=//' maven-archiver/pom.properties)

cd ..
# unpack
mkdir -p target/dependency
(cd target/dependency; jar -xf ../*.jar)

# docker
docker build -t ${artifactId}:${version} -f ./Dockerfile .
docker tag  ${artifactId}:${version}  ${artifactId}:latest


# hub.docker.com/szopen
group=szopen
docker login -u szopen --password
docker tag ${artifactId}:${version} ${group}/${artifactId}:${version}
docker push ${group}/${artifactId}:${version}
docker tag ${artifactId}:${version} ${group}/${artifactId}:latest
#docker push ${group}/${artifactId}:latest