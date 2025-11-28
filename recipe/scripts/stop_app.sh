#!/bin/bash
CURRENT_PID=$(docker ps -a -q --filter name=recipe) # 컨테이너 이름으로 필터링

if [ -n "$CURRENT_PID" ]; then
    echo "Stopping existing Docker container named recipe (ID: $CURRENT_PID)"
    docker stop $CURRENT_PID
    docker rm $CURRENT_PID
else
    echo "No existing Docker container to stop."
fi

# 모든 Docker 이미지 삭제 (선택 사항, 용량 확보 또는 최신 이미지만 사용하기 위함)
# docker rmi $(docker images -q)
