# git
git checkout .
git pull

# maven
mvn clean install -DskipTests=true -Pdev -PeoaServer

# docker
## you should change password in docker.env.dev10
# JDBC_PASSWORD=eap
sudo docker-compose -f dev10-docker-compose.yml up -d --build
docker logs --tail=20 -f eap-server