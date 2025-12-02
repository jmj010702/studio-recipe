#!/bin/bash
set -euo pipefail

echo "--- start_container.sh script initiated ---"

DEPLOY_DIR="/opt/codedeploy-deployment"

ECR_IMAGE=$(tr -d ' \n' < "${DEPLOY_DIR}/ECR_IMAGE_VALUE.txt")
echo "Using ECR image: ${ECR_IMAGE}"

SECRET_ID="recipe-app-secrets" 

echo "Fetching secret: ${SECRET_ID}"

SECRET_JSON=$(aws secretsmanager get-secret-value \
  --secret-id "$SECRET_ID" \
  --query SecretString \
  --output text \
  --region ap-northeast-2)

echo "DEBUG: End of raw output."
echo "DEBUG: Validating if output is valid JSON using 'jq -e .'."
echo "${SECRET_JSON}" | jq -e '.' >/dev/null 2>&1
echo "DEBUG: SecretString successfully validated as valid JSON."
echo "Secrets fetched and parsed successfully."

SPRING_DATASOURCE_USERNAME=$(echo "$SECRET_JSON" | jq -r '.DB_USERNAME')
SPRING_DATASOURCE_PASSWORD=$(echo "$SECRET_JSON" | jq -r '.DB_PASSWORD')
SPRING_MAIL_USERNAME=$(echo "$SECRET_JSON" | jq -r '.MAIL_USERNAME')
SPRING_MAIL_PASSWORD=$(echo "$SECRET_JSON" | jq -r '.MAIL_PASSWORD')
JWT_SECRET=$(echo "$SECRET_JSON" | jq -r '.MY_APP_SECRET')

SPRING_DATASOURCE_URL="jdbc:mariadb://recipe-app-db.c1w8qmkce4t6.ap-northeast-2.rds.amazonaws.com:3306/recipe_db?useUnicode=true&characterEncoding=UTF-8&serverTimezone=UTC"
SPRING_MAIL_HOST="smtp.naver.com"
SPRING_MAIL_PORT="465"
SPRING_DATA_REDIS_HOST="clustercfg.recipe-app-cache.yyo014.apn2.cache.amazonaws.com"
SPRING_DATA_REDIS_PORT="6379"

echo "Logging in to ECR: 516175389011.dkr.ecr.ap-northeast-2.amazonaws.com"
aws ecr get-login-password --region ap-northeast-2 \
  | docker login --username AWS --password-stdin 516175389011.dkr.ecr.ap-northeast-2.amazonaws.com
echo "ECR login successful."

docker rm -f recipe-app-container 2>/dev/null || true

echo "Starting Docker container recipe-app-container with image ${ECR_IMAGE}"

docker run -d \
  --name recipe-app-container \
  -p 8080:8080 \
  -e SPRING_PROFILES_ACTIVE=prod \
  -e SPRING_DATASOURCE_URL="${SPRING_DATASOURCE_URL}" \
  -e SPRING_DATASOURCE_USERNAME="${SPRING_DATASOURCE_USERNAME}" \
  -e SPRING_DATASOURCE_PASSWORD="${SPRING_DATASOURCE_PASSWORD}" \
  -e SPRING_MAIL_HOST="${SPRING_MAIL_HOST}" \
  -e SPRING_MAIL_PORT="${SPRING_MAIL_PORT}" \
  -e SPRING_MAIL_USERNAME="${SPRING_MAIL_USERNAME}" \
  -e SPRING_MAIL_PASSWORD="${SPRING_MAIL_PASSWORD}" \
  -e SPRING_DATA_REDIS_HOST="${SPRING_DATA_REDIS_HOST}" \
  -e SPRING_DATA_REDIS_PORT="${SPRING_DATA_REDIS_PORT}" \
  -e JWT_SECRET="${JWT_SECRET}" \
  -e JAVA_TOOL_OPTIONS="-Djava.net.preferIPv4Stack=true -Djava.net.preferIPv6Addresses=false -Dio.netty.resolver.useNativeCache=false -Dio.netty.resolver.noCache=true" \
  "${ECR_IMAGE}"
