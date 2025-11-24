#!/bin/bash
set -eux 

echo "Cleaning up dangling Docker images and unused layers..."
# docker image prune -f 는 dangling images만 지움.
# docker system prune -f -a 는 dangling images & unused images & stopped containers & unused volumes
# 그러나 ASG에서 실행되므로, 필요한 것만 지우는 것이 좋음.
# dangling images (태그 없이 남은 이미지)만 지우는 것이 목적이라면 prune -f 그대로.
# 모든 사용하지 않는 이미지를 지우려면 'docker image prune -a -f'

docker image prune -f

echo "Finished cleaning old Docker images.
