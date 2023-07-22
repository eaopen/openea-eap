#!/bin/bash

# pom
cd ../

# maven
#mvn clean & install

# eoa
cd eoa-server

# maven
mvn install

# docker
docker build . --tag eoa-server

## dicker login
sh ./openea-docker-login.sh

docker tag eoa-server openea-docker.pkg.coding.net/reps/docker/eoa-server:latest

docker push openea-docker.pkg.coding.net/reps/docker/eoa-server:latest

