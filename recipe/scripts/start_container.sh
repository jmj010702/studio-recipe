#!/bin/bash

# --- 스크립트 실행 시작 알림 ---
echo "--- start_container.sh script initiated ---"
echo "Running as user: $(whoami)"
echo "Current directory: $(pwd)"

# --- 0. jq 설치 확인 (CodeDeploy Agent 환경에 따라 필요할 수 있으나, 보통 기본 설치되어 있음) ---
# 이 부분은 환경마다 다르므로, 필요시 주석을 해제하여 사용하거나, AMI 생성 시 jq를 포함하여 만듭니다.
if ! command -v jq &> /dev/null
then
    echo "jq is not installed. Installing jq..."
    sudo apt update -y || { echo "ERROR: apt update failed."; exit 1; }
    sudo apt install -y jq || { echo "ERROR: Failed to install jq."; exit 1; }
else
    echo "jq is already installed."
fi

# --- 1. 상수 정의 (start_container.sh 스크립트 내에서 사용될 변수들) ---
CONTAINER_NAME="recipe-app-container"
ECR_REGION="ap-northeast-2" # ECR 리전 지정
SECRET_ID="recipe-app-secrets" # AWS Secrets Manager에서 사용할 Secret ID

# --- 2. ECR 이미지 URI 추출 ---
# CodeDeploy 배포 환경에서는 `scripts` 폴더의 상위 폴더가 deployment-archive입니다.
SCRIPT_FULL_PATH=$(readlink -f "$0") # 예: /opt/codedeploy-agent/.../deployment-archive/scripts/start_container.sh
SCRIPT_DIR=$(dirname "$SCRIPT_FULL_PATH") # 예: /opt/codedeploy-agent/.../deployment-archive/scripts
APP_ROOT_DIR=$(dirname "$SCRIPT_DIR") # 예: /opt/codedeploy-agent/.../deployment-archive/ (여기에 ECR_IMAGE_VALUE.txt가 있음)
ECR_IMAGE_FILE_PATH="${APP_ROOT_DIR}/ECR_IMAGE_VALUE.txt"

echo "DEBUG: SCRIPT_FULL_PATH = ${SCRIPT_FULL_PATH}"
echo "DEBUG: SCRIPT_DIR = ${SCRIPT_DIR}"
echo "DEBUG: APP_ROOT_DIR = ${APP_ROOT_DIR}"
echo "DEBUG: ECR_IMAGE_FILE_PATH = ${ECR_IMAGE_FILE_PATH}"

if [ -f "${ECR_IMAGE_FILE_PATH}" ]; then
    ECR_IMAGE=$(cat "${ECR_IMAGE_FILE_PATH}")
    echo "ECR_IMAGE loaded: ${ECR_IMAGE}"
else
    echo "ERROR: ECR_IMAGE_VALUE.txt not found at expected path: ${ECR_IMAGE_FILE_PATH}!"
    exit 1
fi
ECR_REGISTRY=$(echo "${ECR_IMAGE}" | cut -d'/' -f1) # ECR 레지스트리 주소 추출 (로그인용)
echo "DEBUG: ECR_REGISTRY = ${ECR_REGISTRY}"

# --- 3. Secrets Manager에서 비밀값 가져오기 및 파싱 ---
echo "Fetching secrets from AWS Secrets Manager: ${SECRET_ID} in region ${ECR_REGION}"

# aws secretsmanager 명령을 실행하고 표준 출력과 에러를 모두 'SECRET_JSON_OUTPUT' 변수에 저장합니다.
# 'set -o pipefail'은 파이프라인의 명령이 실패하면 전체 파이프라인이 실패하도록 설정합니다.
set -o pipefail
SECRET_JSON_OUTPUT=$(aws secretsmanager get-secret-value --secret-id "${SECRET_ID}" --region "${ECR_REGION}" --query SecretString --output text 2>&1)
set +o pipefail # set -o pipefail 설정 해제 (이후 다른 명령에 영향을 주지 않도록)

echo "DEBUG: Raw output from 'aws secretsmanager get-secret-value' command:"
echo "${SECRET_JSON_OUTPUT}"
echo "DEBUG: End of raw output."

# AWS CLI 명령 실행 자체의 성공/실패를 먼저 검증합니다.
# 만약 'SECRET_JSON_OUTPUT'에 "An error occurred"와 같은 AWS CLI 에러 메시지가 포함되어 있다면 실패로 처리합니다.
if echo "${SECRET_JSON_OUTPUT}" | grep -q "An error occurred"; then
    echo "ERROR: AWS CLI failed to retrieve secrets from Secrets Manager for '${SECRET_ID}'."
    echo "       Please check IAM permissions of this EC2 instance, network connectivity, and secret ID."
    echo "AWS CLI Error Output: ${SECRET_JSON_OUTPUT}" # 상세 에러 메시지 출력
    exit 1
fi

# SecretString이 비어있거나 'null', 'None'인 경우 처리합니다.
# Secrets Manager에 SecretString이 없으면 "None"으로 출력되거나, 아무것도 출력되지 않을 수 있습니다.
if [ -z "${SECRET_JSON_OUTPUT}" ] || [ "${SECRET_JSON_OUTPUT}" == "null" ] || [ "${SECRET_JSON_OUTPUT}" == "None" ]; then
    echo "ERROR: SecretString is empty, null, or not found in Secrets Manager for ID '${SECRET_ID}'."
    echo "       This could mean the secret exists but has no SecretString value, or the value is invalid."
    exit 1
fi

# 이제 SECRET_JSON_OUTPUT이 유효한 JSON 형식인지 간단히 검증합니다.
# 'jq -e .'는 유효한 JSON이면 0을, 아니면 1을 반환합니다.
echo "DEBUG: Validating if output is valid JSON using 'jq -e .'."
if ! echo "${SECRET_JSON_OUTPUT}" | jq -e . > /dev/null 2>&1; then
    echo "ERROR: Retrieved SecretString is not a valid JSON format."
    echo "       This is the most common reason for 'jq: error: is/0 is not defined'."
    echo "       Please ensure the value stored in Secrets Manager for '${SECRET_ID}' is a single, valid JSON object (e.g., {\"key\": \"value\"})."
    echo "       Invalid SecretString Content: ${SECRET_JSON_OUTPUT}"
    exit 1
fi
echo "DEBUG: SecretString successfully validated as valid JSON."

# jq를 사용하여 JSON에서 필요한 값 추출
# 이 키 이름들은 Secrets Manager의 JSON에 저장된 키 이름과 정확히 일치해야 합니다. (대소문자 구분!)
DB_USERNAME=$(echo "${SECRET_JSON_OUTPUT}" | jq -r '.DB_USERNAME')
DB_PASSWORD=$(echo "${SECRET_JSON_OUTPUT}" | jq -r '.DB_PASSWORD')
DB_HOST=$(echo "${SECRET_JSON_OUTPUT}" | jq -r '.DB_HOST')
DB_NAME=$(echo "${SECRET_JSON_OUTPUT}" | jq -r '.DB_NAME')

MAIL_USERNAME=$(echo "${SECRET_JSON_OUTPUT}" | jq -r '.MAIL_USERNAME')
MAIL_PASSWORD=$(echo "${SECRET_JSON_OUTPUT}" | jq -r '.MAIL_PASSWORD')
MAIL_HOST=$(echo "${SECRET_JSON_OUTPUT}" | jq -r '.MAIL_HOST')
MAIL_PORT=$(echo "${SECRET_JSON_OUTPUT}" | jq -r '.MAIL_PORT')

# JWT_SECRET: 이 변수명은 애플리케이션에서 실제로 사용하는 환경 변수명과 일치해야 합니다.
# Secrets Manager에는 'MY_APP_SECRET'이라는 키로 저장되어 있을 것으로 가정합니다.
JWT_SECRET=$(echo "${SECRET_JSON_OUTPUT}" | jq -r '.MY_APP_SECRET')

# 환경 변수가 제대로 추출되었는지 검증 (DEBUG INFO 추가)
if [ -z "${DB_USERNAME}" ] || [ -z "${DB_PASSWORD}" ] || [ -z "${DB_HOST}" ] || [ -z "${DB_NAME}" ] || \
   [ -z "${MAIL_USERNAME}" ] || [ -z "${MAIL_PASSWORD}" ] || [ -z "${MAIL_HOST}" ] || [ -z "${MAIL_PORT}" ] || \
   [ -z "${JWT_SECRET}" ]; then
    echo "ERROR: One or more required secret values could not be extracted or are empty from Secrets Manager."
    echo "       Please check that the JSON keys (e.g., 'DB_USERNAME', 'MY_APP_SECRET') in Secrets Manager match your script and contain non-empty values."
    echo "DEBUG INFO: DB_USERNAME='${DB_USERNAME}', MAIL_USERNAME='${MAIL_USERNAME}', JWT_SECRET='${JWT_SECRET}'"
    echo "Full SecretString JSON was: ${SECRET_JSON_OUTPUT}"
    exit 1
fi

echo "Secrets fetched and parsed successfully."
echo "DEBUG INFO: DB_HOST='${DB_HOST}', MAIL_HOST='${MAIL_HOST}', JWT_SECRET has value (length: ${#JWT_SECRET})." # 실제 값 노출 방지

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

# 기존에 실행 중인 컨테이너가 있다면 중지 및 삭제 (안정성을 위해)
if sudo docker ps -a --format '{{.Names}}' | grep -q "${CONTAINER_NAME}"; then
    echo "Existing container ${CONTAINER_NAME} found. Stopping and removing it."
    sudo docker stop "${CONTAINER_NAME}" || true # 오류 발생해도 스크립트 진행 (이미 중지되었을 수 있음)
    sudo docker rm "${CONTAINER_NAME}" || true # 오류 발생해도 스크립트 진행
fi

# JVM 옵션을 JAVA_TOOL_OPTIONS 환경 변수를 통해 전달
# 이 방식은 Dockerfile의 ENTRYPOINT를 변경하지 않고도 JVM 설정을 적용할 수 있어 견고합니다.
JVM_OPTS="-Djava.net.preferIPv4Stack=true -Djava.net.preferIPv6Addresses=false -Dio.netty.resolver.useNativeCache=false -Dio.netty.resolver.noCache=true"

# Docker 컨테이너 실행
# --network host: 컨테이너가 호스트의 네트워크를 직접 사용 (ALB 연결 등에 필요)
# -e SPRING_PROFILES_ACTIVE="prod": 프로파일 명시적으로 활성화 (Dockerfile ENTRYPOINT에도 지정되어 있으나 명시적 재설정)
sudo docker run -d \
  --name "${CONTAINER_NAME}" \
  # --network host \
  -p 8080:8080 \
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
    echo "ERROR: Failed to run Docker container. Please check Docker daemon status, image existence, or container logs."
    exit 1
fi
echo "Docker container ${CONTAINER_NAME} started successfully."
echo "--- start_container.sh script finished ---"
