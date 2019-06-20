#!/usr/bin/env bash
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
cd ${DIR}

if [ ! -d "dist" ];then
  mkdir dist
fi

cd dist
rm -rf *
cd ..

sh ../tool/npm-build-all.sh

artifactId=eap-admin-ui
version=iview-1.0.0


# docker
docker build -t ${artifactId}:${version} -f ./admin_Dockerfile .
docker tag  ${artifactId}:${version}  ${artifactId}:latest

# hub.docker.com/szopen
group=szopen
#docker login -u szopen --password
docker tag ${artifactId}:${version} ${group}/${artifactId}:${version}
#docker push ${group}/${artifactId}:${version}
docker tag ${artifactId}:${version} ${group}/${artifactId}:latest
#docker push ${group}/${artifactId}:latest


