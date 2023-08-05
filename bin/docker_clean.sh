# view
docker images -f "dangling=true"
# remove
docker rmi $(docker images -f "dangling=true" -q)