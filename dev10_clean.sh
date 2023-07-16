sudo docker-compose -f dev10-docker-compose.yml down


docker image remove eap-server
docker image remove eap-admin
docker image prune -f
docker images