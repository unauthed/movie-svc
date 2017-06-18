#!/usr/bin/env bash

function finish {
  docker-compose down > /dev/null 2>&1
  docker volume rm --force moviesvc_mongo-volume > /dev/null 2>&1
}

trap finish EXIT

echo Build and run this service

mvn clean package docker:build -DskipTests

docker-compose up -d

sleep 8s

xdg-open http://localhost:9090/index.html

docker-compose logs -f
