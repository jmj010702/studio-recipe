#!/bin/bash
set -eux 

echo "Cleaning up dangling Docker images and unused layers..."

# Docker 명령 앞에 sudo를 붙여 권한 문제를 회피하고,
# 명령 실패 시에도 스크립트가 중단되지 않도록 || true 추가
sudo docker image prune -f || true

echo "Finished cleaning old Docker images. (If no images were to be pruned, it's normal.)"
