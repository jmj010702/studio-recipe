#!/bin/bash

# Define constants
CONTAINER_NAME="recipe-app-container"

# ECR_IMAGE_VALUE.txt 파일에서 이미지 URI를 읽어옴
# 이 파일은 Jenkins에서 동적으로 생성되어 CodeDeploy 배포 패키지에 포함
# SCRIPT_DIR=$(dirname "$0") # 스크립트가 위치한 디렉토리
# APP_ROOT_DIR=$(dirname "$SCRIPT_DIR") # 애플리케이션 루트 디렉토리
# ECR_IMAGE_FILE_PATH="${APP_ROOT_DIR}/ECR_IMAGE_VALUE.txt"

# 현재 스크립트의 경로를 절대 경로로 구하고, 그 상위 디렉토리를 애플리케이션 루트로 사용합니다.
SCRIPT_FULL_PATH=$(readlink -f "$0") # 스크립트의 절대 경로
SCRIPT_DIR=$(dirname "$SCRIPT_FULL_PATH") # 스크립트가 위치한 디렉토리
APP_ROOT_DIR=$(dirname "$SCRIPT_DIR") # 애플리케이션 루트 디렉토리 (e.g., /home/ubuntu/recipe-app)
ECR_IMAGE_FILE_PATH="${APP_ROOT_DIR}/ECR_IMAGE_VALUE.txt"

# echo "DEBUG: ----------------------------------------------------"
# echo "DEBUG: Executing scripts/start_container.sh"
# echo "DEBUG: SCRIPT_DIR is: ${SCRIPT_DIR}"
# echo "DEBUG: APP_ROOT_DIR is: ${APP_ROOT_DIR}"
# echo "DEBUG: ECR_IMAGE_FILE_PATH is: ${ECR_IMAGE_FILE_PATH}"
# echo "DEBUG: Files in APP_ROOT_DIR:"
# ls -alF "${APP_ROOT_DIR}"
# echo "DEBUG: Files in SCRIPT_DIR:"
# ls -alF "${SCRIPT_DIR}"
# echo "DEBUG: ----------------------------------------------------"


if [ -f "${ECR_IMAGE_FILE_PATH}" ]; then
    ECR_IMAGE=$(cat "${ECR_IMAGE_FILE_PATH}")
    # echo "DEBUG: ECR_IMAGE read from file (${ECR_IMAGE_FILE_PATH}) is ${ECR_IMAGE}"
else
    echo "ERROR: ECR_IMAGE_VALUE.txt not found at expected path: ${ECR_IMAGE_FILE_PATH}!"
    exit 1
fi

# ECR 레지스트리 경로 추출 (도메인 부분만)
ECR_REGISTRY=$(echo "${ECR_IMAGE}" | cut -d'/' -f1)

echo "Logging in to ECR: ${ECR_REGISTRY}"

# AWS CLI를 사용하여 ECR에 로그인합니다.
# Docker 데몬에 로그인 자격 증명을 전달합니다.
aws ecr get-login-password --region ap-northeast-2 | sudo docker login --username AWS --password-stdin "${ECR_REGISTRY}"
if [ $? -ne 0 ]; then
    echo "ERROR: ECR login failed."
    exit 1
fi
echo "ECR login successful."

# 기존 컨테이너를 중지하고 삭제합니다 (블루/그린 배포 시 기존 컨테이너는 이미 종료되지만, 안전을 위해)
# docker stop ${CONTAINER_NAME} && docker rm ${CONTAINER_NAME}

# 도커 컨테이너 실행
sudo docker run -d \
  --name ${CONTAINER_NAME} \
  -p 8080:8080 \
  -v /var/lib/docker/data:/app/data \
  ${ECR_IMAGE}

if [ $? -ne 0 ]; then
    echo "ERROR: Failed to run Docker container."
    exit 1
fi
echo "Docker container ${CONTAINER_NAME} started successfully with image ${ECR_IMAGE}."
