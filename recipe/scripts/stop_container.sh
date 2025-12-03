#!/bin/bash
set -eux # 에러 발생 시 즉시 중단 및 실행된 명령 출력

CONTAINER_NAME="recipe-app-container" # 컨테이너 이름

echo "Checking for existing container: $CONTAINER_NAME"

# 현재 실행 중인 컨테이너가 있는지 확인하고, 있으면 중지 및 제거
if docker ps | grep -q $CONTAINER_NAME; then
  echo "Container '$CONTAINER_NAME' is running. Stopping it..."
  docker stop $CONTAINER_NAME
  echo "Container '$CONTAINER_NAME' stopped."
fi

# 모든 상태의 컨테이너(실행 중이 아니더라도)가 있는지 확인하고, 있으면 제거
if docker ps -a | grep -q $CONTAINER_NAME; then
  echo "Removing existing container '$CONTAINER_NAME' (if any state)..."
  docker rm $CONTAINER_NAME
  echo "Container '$CONTAINER_NAME' removed."
fi

echo "Finished stopping and removing old container (if any)."
