# git
#git checkout .
#git pull

# maven
mvn clean install -DskipTests=true -Pdev -PeoaServer

# docker
## you should change password in env.dev10.docker
# JDBC_PASSWORD=eap
# 或者sh设置环境变量:
# export JDBC_PASSWORD=eap
## bug will fix, change password in dev10-docker-compose.yml
sudo docker-compose -f dev10-docker-compose.yml up -d --build
docker logs --tail=20 -f eap-server