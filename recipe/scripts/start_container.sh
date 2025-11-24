#!/bin/bash
set -eux

ECR_IMAGE=$ECR_IMAGE
echo "🐳 Deploying Docker Image: $ECR_IMAGE"

aws ecr get-login-password --region ap-northeast-2 \
| docker login --username AWS --password-stdin 516175389011.dkr.ecr.ap-northeast-2.amazonaws.com


# 2. Docker 이미지를 다운로드(pull)합니다.
docker pull $ECR_IMAGE


SECRET_STRING=$(aws secretsmanager get-secret-value --secret-id recipe-app-secrets --query SecretString --output text --region ap-northeast-2)

DB_HOST=$(echo "$SECRET_STRING" | jq -r '.DATABASE_HOST')
DB_PORT=$(echo "$SECRET_STRING" | jq -r '.DATABASE_PORT')
DB_USER=$(echo "$SECRET_STRING" | jq -r '.DATABASE_USER')
DB_PASSWORD=$(echo "$SECRET_STRING" | jq -r '.DATABASE_PASSWORD')

MAIL_USERNAME=$(echo "$SECRET_STRING" | jq -r '.MAIL_USERNAME') 
MAIL_PASSWORD=$(echo "$SECRET_STRING" | jq -r '.MAIL_PASSWORD') 

REDIS_PORT=$(echo "$SECRET_STRING" | jq -r '.REDIS_PORT')
REDIS_HOST=$(echo "$SECRET_STRING" | jq -r '.REDIS_HOST') 

MY_APP_SECRET=$(echo "$SECRET_STRING" | jq -r '.MY_APP_SECRET')



ENV_ARGS=""

ENV_ARGS+=" -e DRIVER_URL='jdbc:mariadb://${DB_HOST}:${DB_PORT}/recipe_db?useSSL=false&allowPublicKeyRetrieval=true'"
ENV_ARGS+=" -e DRIVER_USER_NAME=${DB_USER}"
ENV_ARGS+=" -e DRIVER_PASSWORD=${DB_PASSWORD}"

ENV_ARGS+=" -e REDIS_HOST=${REDIS_HOST}" 
ENV_ARGS+=" -e REDIS_PORT=${REDIS_PORT}"

ENV_ARGS+=" -e MAIL_USERNAME=${MAIL_USERNAME}"
ENV_ARGS+=" -e MAIL_PASSWORD=${MAIL_PASSWORD}"

ENV_ARGS+=" -e MY_APP_SECRET=${MY_APP_SECRET}"

# Spring 프로파일 설정
ENV_ARGS+=" -e SPRING_PROFILES_ACTIVE=prod" # prod 프로파일 활성화


# 5. Docker 컨테이너 실행
CONTAINER_NAME="recipe-app-container"
if docker ps -a | grep -q $CONTAINER_NAME; then
  echo "Stopping and removing existing container: $CONTAINER_NAME"
  docker stop $CONTAINER_NAME
  docker rm $CONTAINER_NAME
fi

docker run -d \
  --name $CONTAINER_NAME \
  --restart always \
  -p 8080:8080 \
  $ENV_ARGS \
  $ECR_IMAGE

echo "🚀 Docker container '$CONTAINER_NAME' started successfully with image '$ECR_IMAGE' on port 8080."
