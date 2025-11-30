#!/bin/bash

# --- 0. 필요 툴 설치 확인
# apt-get을 사용하기 전에 업데이트를 수행
sudo apt update -y

# jq가 설치되어 있지 않으면 설치
if ! command -v jq &> /dev/null
then
    echo "jq is not installed. Installing jq..."
    sudo apt install -y jq
    if [ $? -ne 0 ]; then
        echo "ERROR: Failed to install jq. Please install it manually."
        exit 1
    fi
else
    echo "jq is already installed."
fi

# --- 1. 상수 정의 ---
CONTAINER_NAME="recipe-app-container"
ECR_REGION="ap-northeast-2" # ECR 리전 지정
SECRET_ID="recipe-app-secrets"

# 2. ECR 이미지 URI 추출
SCRIPT_FULL_PATH=$(readlink -f "$0")
SCRIPT_DIR=$(dirname "$SCRIPT_FULL_PATH")
APP_ROOT_DIR=$(dirname "$SCRIPT_DIR")
ECR_IMAGE_FILE_PATH="${APP_ROOT_DIR}/ECR_IMAGE_VALUE.txt"

if [ -f "${ECR_IMAGE_FILE_PATH}" ]; then
    ECR_IMAGE=$(cat "${ECR_IMAGE_FILE_PATH}")
else
    echo "ERROR: ECR_IMAGE_VALUE.txt not found at expected path: ${ECR_IMAGE_FILE_PATH}!"
    exit 1
fi
ECR_REGISTRY=$(echo "${ECR_IMAGE}" | cut -d'/' -f1)

# 3. Secrets Manager에서 비밀값 가져오기 및 파싱
echo "Fetching secrets from AWS Secrets Manager: ${SECRET_ID}"
SECRET_JSON=$(aws secretsmanager get-secret-value --secret-id "${SECRET_ID}" --region "${ECR_REGION}" --query SecretString --output text)

if [ $? -ne 0 ]; then
    echo "ERROR: Failed to retrieve secrets from Secrets Manager. Check IAM permissions and secret ID."
    exit 1
fi

# jq를 사용하여 JSON에서 필요한 값 추출
DB_USERNAME=$(echo "${SECRET_JSON}" | jq -r '.DB_USERNAME')
DB_PASSWORD=$(echo "${SECRET_JSON}" | jq -r '.DB_PASSWORD')
DB_HOST=$(echo "${SECRET_JSON}" | jq -r '.DB_HOST')
DB_NAME=$(echo "${SECRET_JSON}" | jq -r '.DB_NAME')

MAIL_USERNAME=$(echo "${SECRET_JSON}" | jq -r '.MAIL_USERNAME')
MAIL_PASSWORD=$(echo "${SECRET_JSON}" | jq -r '.MAIL_PASSWORD')
MAIL_HOST=$(echo "${SECRET_JSON}" | jq -r '.MAIL_HOST')
MAIL_PORT=$(echo "${SECRET_JSON}" | jq -r '.MAIL_PORT')

MY_APP_SECRET=$(echo "${SECRET_JSON}" | jq -r '.MY_APP_SECRET')

# 환경 변수가 제대로 추출되었는지 간단한 검증
if [ -z "${DB_USERNAME}" ] || [ -z "${DB_PASSWORD}" ] || [ -z "${DB_HOST}" ] || [ -z "${DB_NAME}" ] || \
   [ -z "${MAIL_USERNAME}" ] || [ -z "${MAIL_PASSWORD}" ] || [ -z "${MAIL_HOST}" ] || [ -z "${MAIL_PORT}" ] || \
   [ -z "${MY_APP_SECRET}" ]; then
    echo "ERROR: One or more required secret values could not be extracted or are empty. Check Secrets Manager JSON structure."
    echo "DEBUG INFO: DB_USERNAME=[${DB_USERNAME}], MAIL_USERNAME=[${MAIL_USERNAME}], MY_APP_SECRET=[${MY_APP_SECRET}]]"
    exit 1
fi

echo "Secrets fetched successfully. DEBUG INFO: MAIL_USERNAME=[${MAIL_USERNAME}], MY_APP_SECRET=[${MY_APP_SECRET}]]"

# --- 4. ECR 로그인 ---
echo "Logging in to ECR: ${ECR_REGISTRY}"
aws ecr get-login-password --region "${ECR_REGION}" | sudo docker login --username AWS --password-stdin "${ECR_REGISTRY}"
if [ $? -ne 0 ]; then
    echo "ERROR: ECR login failed."
    exit 1
fi
echo "ECR login successful."

# 5. 도커 컨테이너 실행
echo "Starting Docker container ${CONTAINER_NAME} with image ${ECR_IMAGE}"
sudo docker run -d \
  --name "${CONTAINER_NAME}" \
  --network host \
  -e SPRING_DATASOURCE_URL="jdbc:mariadb://${DB_HOST}:3306/${DB_NAME}?useUnicode=true&characterEncoding=UTF-8&serverTimezone=UTC" \
  -e SPRING_DATASOURCE_USERNAME="${DB_USERNAME}" \
  -e SPRING_DATASOURCE_PASSWORD="${DB_PASSWORD}" \
  -e SPRING_MAIL_HOST="${MAIL_HOST}" \
  -e SPRING_MAIL_PORT="${MAIL_PORT}" \
  -e SPRING_MAIL_USERNAME="${MAIL_USERNAME}" \
  -e SPRING_MAIL_PASSWORD="${MAIL_PASSWORD}" \
  -e MY_APP_SECRET="${MY_APP_SECRET}" \
  -e SPRING_DATA_REDIS_HOST="clustercfg.recipe-app-cache.yyo014.apn2.cache.amazonaws.com" \
  -v /var/lib/docker/data:/app/data \
  "${ECR_IMAGE}" \
  java -Djava.net.preferIPv4Stack=true -jar /app/app.jar
  # Redis Cluster Ipv6로 찾아서 문제, JVM이 Ipv4 우선적으로 찾게 함

if [ $? -ne 0 ]; then
    echo "ERROR: Failed to run Docker container."
    exit 1
fi
echo "Docker container ${CONTAINER_NAME} started successfully."
