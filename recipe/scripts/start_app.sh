#!/bin/bash

# Jenkinsfile에서 CodeDeploy를 통해 넘어온 환경 변수 (Jenkinsfile 참고)
ECR_REPO_URL="${ECR_REPO_URL}" # 'ACCOUNT_ID.dkr.ecr.ap-northeast-2.amazonaws.com/recipe-app-backend'
IMAGE_TAG="${IMAGE_TAG}"       # 'BUILD_NUMBER'
ENV_VARS="${ENV_VARS_FOR_DOCKER}" # Secrets Manager에서 가져온 환경 변수들 (ex: "-e KEY1=VALUE1 -e KEY2=VALUE2")

echo "Attempting to pull and run Docker image: $ECR_REPO_URL:$IMAGE_TAG"

# ECR 로그인
aws ecr get-login-password --region ap-northeast-2 | docker login --username AWS --password-stdin "$ECR_REPO_URL"

# 기존 이미지 삭제 (용량 확보)
if docker images | grep -q "$ECR_REPO_URL"; then
    echo "Removing existing Docker images for $ECR_REPO_URL"
    docker rmi $(docker images "$ECR_REPO_URL" -q) || true # 실패해도 진행하도록 || true 추가
fi

echo "Pulling Docker image: $ECR_REPO_URL:$IMAGE_TAG"
docker pull "$ECR_REPO_URL:$IMAGE_TAG"

echo "Starting Docker container named recipe with ENV_VARS: $ENV_VARS"
docker run -d \
  -p 8080:8080 \
  --name recipe \
  --restart=on-failure \
  $ENV_VARS \
  "$ECR_REPO_URL:$IMAGE_TAG"

echo "Application recipe started on port 8080"