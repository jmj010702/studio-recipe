#!/bin/bash

# --- 스크립트 실행 시작 알림 ---
echo "--- start_container.sh script initiated ---"
echo "Running as user: $(whoami)"
echo "Current directory: $(pwd)" # 이 로그를 통해 스크립트가 실행되는 실제 디렉토리를 확인합니다.

# 스크립트 실행 중 오류 발생 시 즉시 종료 (파이프라인 전체 실패)
set -eo pipefail

# --- 0. jq 설치 확인 ---
if ! command -v jq &> /dev/null
then
    echo "jq is not installed. Attempting to install jq..."
    sudo apt update -y || { echo "ERROR: apt update failed. Please ensure apt is working."; exit 1; }
    sudo apt install -y jq || { echo "ERROR: Failed to install jq. Please install it manually or check repository access."; exit 1; }
else
    echo "jq is already installed."
fi

# --- 1. 상수 정의 ---
CONTAINER_NAME="recipe-app-container"
ECR_REGION="ap-northeast-2"
SECRET_ID="recipe-app-secrets" # Secrets Manager의 Secret ID

# --- 2. ECR 이미지 URI 추출 ---
# appspec.yml에서 스크립트가 /opt/codedeploy-deployment/scripts/start_container.sh 로 실행되므로,
# ECR_IMAGE_VALUE.txt는 /opt/codedeploy-deployment/ECR_IMAGE_VALUE.txt 에 있습니다.
# 스크립트 실행 디렉토리 (Current directory)가 /opt/codedeploy-deployment/scripts가 아닌
# /opt/codedeploy-deployment 에서 실행되도록 appspec.yml을 수정합니다.
# 이렇게 되면 ECR_IMAGE_FILE_PATH는 './ECR_IMAGE_VALUE.txt'가 됩니다.
# BUT, 현재 appspec.yml hooks location이 절대경로로 지정되어 있기 때문에,
# 현재 디렉토리 (cwd)를 스크립트 시작 부분에서 확인하고, ECR_IMAGE_VALUE.txt의 절대경로를 추론해야 합니다.

# CodeDeploy 에이전트는 훅 스크립트를 /opt/codedeploy-agent/deployment-root/.../deployment-archive/scripts 디렉토리에서 실행하는 경우가 많습니다.
# ECR_IMAGE_VALUE.txt는 deployment-archive/ 루트에 있습니다.
# 따라서 REVISION_LOCATION 대신 ECR_IMAGE_VALUE.txt가 있는 절대 경로를 명시적으로 지정합니다.
# appspec.yml에 destination: /opt/codedeploy-deployment 로 했으므로, 파일은 저기로 복사됩니다.
# 따라서 스크립트가 실행되는 워킹 디렉토리와 무관하게, 항상 파일이 복사된 절대 경로를 참조합니다.
ECR_IMAGE_FILE_PATH="/opt/codedeploy-deployment/ECR_IMAGE_VALUE.txt"


echo "DEBUG: ECR_IMAGE_FILE_PATH = ${ECR_IMAGE_FILE_PATH}"

if [ -f "${ECR_IMAGE_FILE_PATH}" ]; then
    ECR_IMAGE=$(cat "${ECR_IMAGE_FILE_PATH}")
    echo "ECR_IMAGE loaded: ${ECR_IMAGE}"
else
    echo "ERROR: ECR_IMAGE_VALUE.txt not found at expected path: ${ECR_IMAGE_FILE_PATH}!"
    echo "       Please ensure ECR_IMAGE_VALUE.txt is included in your CodeDeploy bundle."
    echo "       And make sure appspec.yml has 'destination: /opt/codedeploy-deployment' for files."
    exit 1
fi
ECR_REGISTRY=$(echo "${ECR_IMAGE}" | cut -d'/' -f1) # ECR 레지스트리 주소 추출 (로그인용)
echo "DEBUG: ECR_REGISTRY = ${ECR_REGISTRY}"

# --- 3. Secrets Manager에서 비밀값 가져오기 및 파싱 ---
echo "Fetching secrets from AWS Secrets Manager: ${SECRET_ID} in region ${ECR_REGION}"

SECRET_JSON_OUTPUT=$(aws secretsmanager get-secret-value --secret-id "${SECRET_ID}" --region "${ECR_REGION}" --query SecretString --output text 2>&1)

echo "DEBUG: Raw output from 'aws secretsmanager get-secret-value' command:"
echo "${SECRET_JSON_OUTPUT}"
echo "DEBUG: End of raw output."

if echo "${SECRET_JSON_OUTPUT}" | grep -q "An error occurred"; then
    echo "ERROR: AWS CLI failed to retrieve secrets from Secrets Manager for '${SECRET_ID}'."
    echo "       Please check IAM permissions of this EC2 instance, network connectivity, and secret ID."
    echo "       AWS CLI Error Output: ${SECRET_JSON_OUTPUT}"
    exit 1
fi

if [ -z "${SECRET_JSON_OUTPUT}" ] || [ "${SECRET_JSON_OUTPUT}" == "null" ] || [ "${SECRET_JSON_OUTPUT}" == "None" ]; then
    echo "ERROR: SecretString is empty, null, or not found in Secrets Manager for ID '${SECRET_ID}'."
    echo "       This could mean the secret exists but has no SecretString value, or the value is invalid."
    exit 1
fi

echo "DEBUG: Validating if output is valid JSON using 'jq -e .'."
if ! echo "${SECRET_JSON_OUTPUT}" | jq -e . > /dev/null 2>&1; then
    echo "ERROR: Retrieved SecretString is not a valid JSON format."
    echo "       This is the most common reason for 'jq: error: ... is not defined' (if not an AWS CLI error)."
    echo "       Please ensure the value stored in Secrets Manager for '${SECRET_ID}' is a single, valid JSON object (e.g., {\"key\": \"value\"})."
    echo "       Invalid SecretString Content: ${SECRET_JSON_OUTPUT}"
    exit 1
fi
echo "DEBUG: SecretString successfully validated as valid JSON."

DB_USERNAME=$(echo "${SECRET_JSON_OUTPUT}" | jq -r '.DB_USERNAME')
DB_PASSWORD=$(echo "${SECRET_JSON_OUTPUT}" | jq -r '.DB_PASSWORD')
DB_HOST=$(echo "${SECRET_JSON_OUTPUT}" | jq -r '.DB_HOST')
DB_NAME=$(echo "${SECRET_JSON_OUTPUT}" | jq -r '.DB_NAME')

MAIL_USERNAME=$(echo "${SECRET_JSON_OUTPUT}" | jq -r '.MAIL_USERNAME')
MAIL_PASSWORD=$(echo "${SECRET_JSON_OUTPUT}" | jq -r '.MAIL_PASSWORD')
MAIL_HOST=$(echo "${SECRET_JSON_OUTPUT}" | jq -r '.MAIL_HOST')
MAIL_PORT=$(echo "${SECRET_JSON_OUTPUT}" | jq -r '.MAIL_PORT')

JWT_SECRET=$(echo "${SECRET_JSON_OUTPUT}" | jq -r '.MY_APP_SECRET')

if [ -z "${DB_USERNAME}" ] || [ -z "${DB_PASSWORD}" ] || [ -z "${DB_HOST}" ] || [ -z "${DB_NAME}" ] || \
   [ -z "${MAIL_USERNAME}" ] || [ -z "${MAIL_PASSWORD}" ] || [ -z "${MAIL_HOST}" ] || [ -z "${MAIL_PORT}" ] || \
   [ -z "${JWT_SECRET}" ]; then
    echo "ERROR: One or more required secret values could not be extracted or are empty from Secrets Manager."
    echo "       Please check that the JSON keys (e.g., 'DB_USERNAME', 'MY_APP_SECRET') in Secrets Manager match your script and contain non-empty values."
    echo "DEBUG INFO: DB_USERNAME='${DB_USERNAME}', DB_HOST='${DB_HOST}', MAIL_HOST='${MAIL_HOST}', JWT_SECRET has value (length: ${#JWT_SECRET})."
    echo "       Full SecretString JSON was: ${SECRET_JSON_OUTPUT}"
    exit 1
fi

echo "Secrets fetched and parsed successfully."

# --- 4. ECR 로그인 ---
echo "Logging in to ECR: ${ECR_REGISTRY}"
sudo aws ecr get-login-password --region "${ECR_REGION}" | sudo docker login --username AWS --password-stdin "${ECR_REGISTRY}"
if [ $? -ne 0 ]; then
    echo "ERROR: ECR login failed. Check AWS CLI configuration and IAM permissions for ECR."
    exit 1
fi
echo "ECR login successful."

# --- 5. 도커 컨테이너 실행 ---
echo "Starting Docker container ${CONTAINER_NAME} with image ${ECR_IMAGE}"

if sudo docker ps -a --format '{{.Names}}' | grep -q "${CONTAINER_NAME}"; then
    echo "Existing container ${CONTAINER_NAME} found. Stopping and removing it."
    sudo docker stop "${CONTAINER_NAME}" || true
    sudo docker rm "${CONTAINER_NAME}" || true
fi

JVM_OPTS="-Djava.net.preferIPv4Stack=true -Djava.net.preferIPv6Addresses=false -Dio.netty.resolver.useNativeCache=false -Dio.netty.resolver.noCache=true"

echo "Running Docker container with following environment variables and JVM options:"
echo "SPRING_DATASOURCE_URL=jdbc:mariadb://${DB_HOST}:3306/${DB_NAME}?useUnicode=true&characterEncoding=UTF-8&serverTimezone=UTC"
echo "SPRING_DATASOURCE_USERNAME=${DB_USERNAME}"
echo "SPRING_MAIL_HOST=${MAIL_HOST}"
echo "SPRING_MAIL_PORT=${MAIL_PORT}"
echo "SPRING_MAIL_USERNAME=${MAIL_USERNAME}"
echo "SPRING_DATA_REDIS_HOST=clustercfg.recipe-app-cache.yyo014.apn2.cache.amazonaws.com"
echo "SPRING_DATA_REDIS_PORT=6379"
echo "SPRING_PROFILES_ACTIVE=prod"
echo "JAVA_TOOL_OPTIONS=${JVM_OPTS}"

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
  -e JWT_SECRET="${JWT_SECRET}" \
  -e SPRING_DATA_REDIS_HOST="clustercfg.recipe-app-cache.yyo014.apn2.cache.amazonaws.com" \
  -e SPRING_DATA_REDIS_PORT="6379" \
  -e SPRING_PROFILES_ACTIVE="prod" \
  -e JAVA_TOOL_OPTIONS="${JVM_OPTS}" \
  "${ECR_IMAGE}"

if [ $? -ne 0 ]; then
    echo "ERROR: Failed to run Docker container. Please check Docker daemon status, image existence, or container logs for detailed errors."
    exit 1
fi
echo "Docker container ${CONTAINER_NAME} started successfully."

sleep 10
CONTAINER_STATUS=$(sudo docker ps -a --filter "name=${CONTAINER_NAME}" --format "{{.Status}}")
echo "DEBUG: Initial status of ${CONTAINER_NAME} after 10s: ${CONTAINER_STATUS}"
if echo "${CONTAINER_STATUS}" | grep -q "Exited"; then
    echo "WARNING: ${CONTAINER_NAME} exited shortly after starting. Checking logs for details."
    sudo docker logs "${CONTAINER_NAME}"
    echo "ERROR: Container ${CONTAINER_NAME} failed to stay running after start. Please check container logs for application-specific errors."
    exit 1
fi

echo "--- start_container.sh script finished ---"
