#!/bin/bash

# pom
cd ../

# maven
mvn install -DskipTests=true

# eoa
cd eoa-server

# maven
mvn clean
mvn install -DskipTests=true

# docker
docker build . --tag eoa-server

## dicker login
sh ./openea-docker-login.sh

docker tag eoa-server openea-docker.pkg.coding.net/reps/docker/eoa-server:latest

docker push openea-docker.pkg.coding.net/reps/docker/eoa-server:latest

