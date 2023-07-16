# git
git checkout .
git pull

# maven
mvn clean install -DskipTests=true -Pdev -PeoaServer

# docker
## you should change password in docker.env.dev10
sudo docker-compose -f docker-compose-dev10.yml up -d --build
