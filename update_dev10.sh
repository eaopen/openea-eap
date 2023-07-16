

mvn clean install -DskipTests=true -Pdev -PeoaServer

## you should change password in docker-compose-dev10.yml
sudo docker-compose -f docker-compose-dev10.yml up -d --build
