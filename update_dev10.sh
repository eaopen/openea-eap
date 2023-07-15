

mvn clean install -DskipTests=true -Pdev -PeoaServer


sudo docker-compose -f docker-compose-dev10.yml up -d
