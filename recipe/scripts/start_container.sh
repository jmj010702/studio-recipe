```bash
#!/bin/bash

# --- 0. 필요 툴 설치 확인 (jq는 이미 설치되었을 것이므로 불필요한 업데이트 방지)
echo "jq is assumed to be installed and available."


# --- 1. 상수 정의 ---
CONTAINER_NAME="recipe-app-container"
ECR_REGION="ap-northeast-2" # ECR 리전 지정
SECRET_ID="recipe-app-secrets"

# 2. ECR 이미지 URI 추출
SCRIPT_FULL_PATH=$(readlink -f "$0")
SCRIPT_DIR=$(dirname "$SCRIPT_FULL_PATH")
APP_ROOT_DIR="${SCRIPT_DIR}" # /deployment-archive 경로가 app.jar 등을 포함하는 root입니다.
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

# JWT_SECRET: 이 변수명은 애플리케이션의 application.yml이나 @Value 필드에서 사용하는 실제 환경 변수 이름과 일치해야 합니다.
# Secrets Manager의 키 이름이 'MY_APP_SECRET'이라면 jq는 .MY_APP_SECRET 로 추출
# 애플리케이션에서는 JWT_SECRET 이라는 이름으로 읽어오는 경우, 환경 변수도 JWT_SECRET으로 설정.
JWT_SECRET=$(echo "${SECRET_JSON}" | jq -r '.MY_APP_SECRET') # Secrets Manager의 키가 MY_APP_SECRET이라면.

# 환경 변수가 제대로 추출되었는지 간단한 검증
if [ -z "${DB_USERNAME}" ] || [ -z "${DB_PASSWORD}" ] || [ -z "${DB_HOST}" ] || [ -z "${DB_NAME}" ] || \
   [ -z "${MAIL_USERNAME}" ] || [ -z "${MAIL_PASSWORD}" ] || [ -z "${MAIL_HOST}" ] || [ -z "${MAIL_PORT}" ] || \
   [ -z "${JWT_SECRET}" ]; then # MY_APP_SECRET 대신 실제 애플리케이션이 사용하는 변수명으로 변경
    echo "ERROR: One or more required secret values could not be extracted or are empty. Check Secrets Manager JSON structure."
    echo "DEBUG INFO: DB_USERNAME=[${DB_USERNAME}], MAIL_USERNAME=[${MAIL_USERNAME}], JWT_SECRET=[${JWT_SECRET}]]"
    exit 1
fi

echo "Secrets fetched successfully. DEBUG INFO: MAIL_USERNAME=[${MAIL_USERNAME}], JWT_SECRET=[${JWT_SECRET}]]"

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

# JVM 옵션을 JAVA_TOOL_OPTIONS 환경 변수를 통해 전달
# 이 방식은 Dockerfile의 ENTRYPOINT를 변경하지 않고도 JVM 설정을 적용할 수 있어 견고합니다.
JVM_OPTS="-Djava.net.preferIPv4Stack=true -Djava.net.preferIPv6Addresses=false -Dio.netty.resolver.useNativeCache=false -Dio.netty.resolver.noCache=true"

sudo docker run -d --name "${CONTAINER_NAME}" --network host \
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
  -e JAVA_TOOL_OPTIONS="${JVM_OPTS}" \ # 여기에 JVM 옵션을 전달
  -v /var/lib/docker/data:/app/data \
  "${ECR_IMAGE}"

if [ $? -ne 0 ]; then
    echo "ERROR: Failed to run Docker container."
    exit 1
fi
echo "Docker container ${CONTAINER_NAME} started successfully."
```
