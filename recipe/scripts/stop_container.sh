#!/bin/bash
CONTAINER_NAME="recipe-app-container" # 컨테이너 이름

if docker ps -a | grep -q $CONTAINER_NAME; then
  echo "Stopping existing container: $CONTAINER_NAME"
  docker stop $CONTAINER_NAME
  docker rm $CONTAINER_NAME
fi
