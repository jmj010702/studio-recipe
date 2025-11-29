#!/bin/bash

# Define constants
CONTAINER_NAME="recipe-app-container"

# ECR_IMAGE_VALUE.txt 파일에서 이미지 URI를 읽어옴
# CodeDeploy 에이전트가 스크립트를 실행하는 경로는 deployment-archive 하위
# ECR_IMAGE_VALUE.txt 파일은 deployment-archive 최상위에 복사
if [ -f "ECR_IMAGE_VALUE.txt" ]; then
    ECR_IMAGE=$(cat ECR_IMAGE_VALUE.txt)
    echo "DEBUG: ECR_IMAGE read from file is ${ECR_IMAGE}"
else
    echo "ERROR: ECR_IMAGE_VALUE.txt not found!"
    exit 1
fi

# ECR 로그인
ECR_REGISTRY=$(echo "${ECR_IMAGE}" | cut -d'/' -f1)

echo "DEBUG: ECR_IMAGE is ${ECR_IMAGE}"
echo "DEBUG: ECR_REGISTRY is ${ECR_REGISTRY}"

echo "Logging in to ECR: ${ECR_REGISTRY}"
aws ecr get-login-password --region ap-northeast-2 | sudo docker login --username AWS --password-stdin "${ECR_REGISTRY}"
if [ $? -ne 0 ]; then
    echo "ERROR: ECR login failed."
    exit 1
fi
echo "ECR login successful."

# ==============================================================================
# SECRET_ID="recipe-app-secrets"
# Secrets Manager가 위치한 AWS 리전
# REGION="ap-northeast-2" 

# echo "Fetching secrets from AWS Secrets Manager for ${SECRET_ID} in ${REGION}..."
# SECRET_STRING=$(aws secretsmanager get-secret-value --secret-id ${SECRET_ID} --region ${REGION} --query SecretString --output text)

# if [ $? -ne 0 ]; then
#     echo "ERROR: Failed to retrieve secrets from Secrets Manager. Check AWS CLI configuration and permissions."
#     exit 1
# fi

# # Secrets Manager JSON 파싱 및 개별 변수 할당
# DB_HOST=$(echo "$SECRET_STRING" | jq -r '.DATABASE_HOST')
# DB_PORT=$(echo "$SECRET_STRING" | jq -r '.DATABASE_PORT')
# DB_USER=$(echo "$SECRET_STRING" | jq -r '.DATABASE_USER')
# DB_PASSWORD=$(echo "$SECRET_STRING" | jq -r '.DATABASE_PASSWORD')

# MAIL_USERNAME=$(echo "$SECRET_STRING" | jq -r '.MAIL_USERNAME')
# MAIL_PASSWORD=$(echo "$SECRET_STRING" | jq -r '.MAIL_PASSWORD')

# REDIS_HOST=$(echo "$SECRET_STRING" | jq -r '.REDIS_HOST')
# REDIS_PORT=$(echo "$SECRET_STRING" | jq -r '.REDIS_PORT')

# MY_APP_SECRET=$(echo "$SECRET_STRING" | jq -r '.MY_APP_SECRET')


# ==============================================================================
# 하드코딩된 값으로 DB/Redis/Mail/APP_SECRET 변수 할당 (Secrets Manager 우회)


echo "DEBUG: Using hardcoded DB/Redis/Mail/APP_SECRET values for testing."

DB_HOST="recipe-app-db.c1w8qmkce4t6.ap-northeast-2.rds.amazonaws.com" 
DB_PORT="3306"
DB_USER="admin"   
DB_PASSWORD="tlwkrdmldkdlA!" 

REDIS_HOST="clustercfg.recipe-app-cache.yyo014.apn2.cache.amazonaws.com:6379" 
REDIS_PORT="6379"

MAIL_USERNAME="stay_on_track@naver.com"
MAIL_PASSWORD="KVRG8UGYM9ZJ"


MY_APP_SECRET="dI5pBjrtgy9xFHiZtMs3fM7P8OR/wvxrexu/mybWcKc=" 

# ==============================================================================


# Debugging: 개별 DB/Redis/Mail/APP_SECRET 변수들이 제대로 할당되었는지 확인
echo "DEBUG: Assigned DB_HOST=${DB_HOST}, DB_PORT=${DB_PORT}, DB_USER=${DB_USER}"
echo "DEBUG: Assigned REDIS_HOST=${REDIS_HOST}, REDIS_PORT=${REDIS_PORT}"
echo "DEBUG: Assigned MAIL_USERNAME=${MAIL_USERNAME}, MAIL_PASSWORD=${MAIL_PASSWORD}"
echo "DEBUG: Assigned MY_APP_SECRET=${MY_APP_SECRET}"


# Docker 컨테이너에 전달할 환경 변수 문자열 빌드
ENV_ARGS=""
ENV_ARGS+=" -e DRIVER_URL='jdbc:mariadb://${DB_HOST}:${DB_PORT}/recipe_db?useSSL=false&allowPublicKeyRetrieval=true'"
ENV_ARGS+=" -e DRIVER_USER_NAME=${DB_USER}"
ENV_ARGS+=" -e DRIVER_PASSWORD=${DB_PASSWORD}"

ENV_ARGS+=" -e MAIL_USERNAME=${MAIL_USERNAME}"
ENV_ARGS+=" -e MAIL_PASSWORD=${MAIL_PASSWORD}"

ENV_ARGS+=" -e SPRING_REDIS_HOST=${REDIS_HOST}" 
ENV_ARGS+=" -e SPRING_REDIS_PORT=${REDIS_PORT}"

ENV_ARGS+=" -e MY_APP_SECRET=${MY_APP_SECRET}"
ENV_ARGS+=" -e SPRING_PROFILES_ACTIVE=prod"

# Debugging: 최종 Docker run 명령에 전달될 ENV_ARGS 확인
echo "DEBUG: Final ENV_ARGS for Docker: ${ENV_ARGS}"

# Docker 컨테이너 실행
echo "Starting Docker container: ${CONTAINER_NAME} with image ${ECR_IMAGE}"
sudo docker run -d \
  -p 8080:8080 \
  -p 50000:50000 \
  --name ${CONTAINER_NAME} \
  ${ENV_ARGS} \
  "${ECR_IMAGE}"

if [ $? -ne 0 ]; then
    echo "ERROR: Docker container failed to start."
    exit 1
fi

echo "Docker container ${CONTAINER_NAME} started successfully."
