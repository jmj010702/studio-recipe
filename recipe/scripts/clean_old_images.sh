#!/bin/bash
# 이전 이미지들을 정리하여 디스크 공간을 확보합니다.
echo "Cleaning up old Docker images..."
docker image prune -f
