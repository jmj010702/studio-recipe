#!/bin/bash
set -e

echo "--- clean_old_images.sh script initiated ---"

# 중지된 컨테이너 정리
echo "INFO: Removing stopped containers..."
docker container prune -f || true

# 사용되지 않는 이미지 정리
echo "INFO: Removing unused images..."
docker image prune -a -f || true

# 사용되지 않는 볼륨 정리
echo "INFO: Removing unused volumes..."
docker volume prune -f || true

# 빌더 캐시 정리(선택)
echo "INFO: Removing builder cache..."
docker builder prune -a -f || true

echo "--- clean_old_images.sh completed ---"
