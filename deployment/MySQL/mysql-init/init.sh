#!/bin/bash
set -e

services=(address business order user)

for svc in "${services[@]}"; do
  echo "==> Initializing $svc"
  master="${svc}-master"

  until docker exec $master mysql -uroot -p123123 -e "SELECT 1" &>/dev/null; do
    sleep 2
  done

  docker exec $master mysql -uroot -p123123 -e "CREATE DATABASE IF NOT EXISTS elm;"
  docker exec $master mysql -uroot -p123123 -e "\
    CREATE USER IF NOT EXISTS 'repl'@'%' IDENTIFIED BY 'repl123';\
    GRANT REPLICATION SLAVE ON *.* TO 'repl'@'%';\
    FLUSH PRIVILEGES;"

  master_ip=$(docker inspect -f '{{range.NetworkSettings.Networks}}{{.IPAddress}}{{end}}' $master)

  for i in 1 2; do
    slave="${svc}-slave${i}"
    echo "--> Congifuring the link from $slave to $master"
    docker exec $slave mysql -uroot -p123123 -e "\
      STOP SLAVE; \
      CHANGE MASTER TO MASTER_HOST='$master_ip', MASTER_USER='repl', MASTER_PASSWORD='repl123', MASTER_AUTO_POSITION=1; \
      START SLAVE;"
  done

  echo "==> $svc configured successfully"
done