#!/usr/bin/env bash
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
cd ${DIR}

# copy eapboot-activiti
eapboot_activiti_path=../eapboot-activiti
cp -f ${eapboot_activiti_path}/src/api/activiti.js ./src/api/
cp -Rf ${eapboot_activiti_path}/src/views/activiti ./src/views/

#sh ../tool/npm-run-dev.sh

docker run --rm --name node-build-server-temp-eapboot-admin -v "$(pwd)":/var/www -w /var/www  szopen/node-sass:8-alpine \
  npm install && npm run dev

#docker run --rm -v $(pwd):/var/www catchdigital/node-sass npm install && npm run dev
