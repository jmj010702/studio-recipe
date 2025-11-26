#!/bin/bash
set -eux # -e: 에러 발생 시 즉시 중단, -u: 정의되지 않은 변수 사용 시 에러 (이것 때문에 unbound variable 발생), -x: 실행되는 명령 출력

echo "--- Starting Application: recipe-app-container ---"

ECR_IMAGE=""

# 모든 명령행 인자를 순회하며 '--ECR_IMAGE='로 시작하는 인자를 찾습니다.
for arg in "$@"; do
  if [[ "$arg" == "--ECR_IMAGE="* ]]; then
    ECR_IMAGE="${arg#*=}" # '=' 뒤의 값을 추출하여 ECR_IMAGE에 할당
    break
  fi
done

# ECR_IMAGE가 파싱된 후에도 비어있다면, 현재 환경 변수에 ECR_IMAGE가 있는지 확인합니다.
# (이전에 CodeDeploy가 환경 변수로 주입했을 때의 Fallback)
# 그래도 ECR_IMAGE가 설정되지 않았다면 오류를 발생시킵니다.
: "${ECR_IMAGE:=${ECR_IMAGE_FROM_ENV:-}}" # ECR_IMAGE_FROM_ENV가 정의되지 않았다면 비어있는 문자열로 대체
: "${ECR_IMAGE:?ERROR: ECR_IMAGE variable was not provided via arguments or environment.}"

echo "DEBUG: Final ECR_IMAGE variable used: $ECR_IMAGE"


# Docker 로그인 및 이미지 다운로드
echo "DEBUG: Logging in to ECR..."
aws ecr get-login-password --region ap-northeast-2 \
| sudo docker login --username AWS --password-stdin 516175389011.dkr.ecr.ap-northeast-2.amazonaws.com || true

echo "DEBUG: Pulling Docker image: $ECR_IMAGE"
sudo docker pull "$ECR_IMAGE" || (echo "ERROR: Failed to pull Docker image: $ECR_IMAGE. Exiting." && exit 1)


# Secrets Manager에서 환경 변수 가져오기
echo "DEBUG: Fetching secrets from AWS Secrets Manager..."
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

# ENV_ARGS 문자열 빌드
ENV_ARGS=""
ENV_ARGS+=" -e DRIVER_URL='jdbc:mariadb://${DB_HOST}:${DB_PORT}/recipe_db?useSSL=false&allowPublicKeyRetrieval=true'"
ENV_ARGS+=" -e DRIVER_USER_NAME=${DB_USER}"
ENV_ARGS+=" -e DRIVER_PASSWORD=${DB_PASSWORD}"

ENV_ARGS+=" -e REDIS_HOST=${REDIS_HOST}"
ENV_ARGS+=" -e REDIS_PORT=${REDIS_PORT}"

ENV_ARGS+=" -e MAIL_USERNAME=${MAIL_USERNAME}"
ENV_ARGS+=" -e MAIL_PASSWORD=${MAIL_PASSWORD}"

ENV_ARGS+=" -e MY_APP_SECRET=${MY_APP_SECRET}"

ENV_ARGS+=" -e SPRING_PROFILES_ACTIVE=prod"


# 기존 컨테이너 정리 로직
CONTAINER_NAME="recipe-app-container"
echo "DEBUG: Checking for existing container '$CONTAINER_NAME'..."
if sudo docker ps -a --format '{{.Names}}' | grep -q "^${CONTAINER_NAME}$"; then
  echo "DEBUG: Stopping and removing existing container: $CONTAINER_NAME"
  sudo docker stop "$CONTAINER_NAME" || true
  sudo docker rm "$CONTAINER_NAME" || true
else
  echo "DEBUG: No existing container '$CONTAINER_NAME' found. Skipping stop/remove."
fi


# 새 Docker 컨테이너 실행
echo "DEBUG: Running new Docker container '$CONTAINER_NAME' with image '$ECR_IMAGE' and environment variables..."
sudo docker run -d \
  -p 8080:8080 \
  --name "$CONTAINER_NAME" \
  --health-cmd="curl -f http://localhost:8080/studio-recipe/health || exit 1" \
  --health-interval=30s \
  --health-timeout=10s \
  --health-retries=3 \
  $ENV_ARGS \
  "$ECR_IMAGE"

echo "Docker container '$CONTAINER_NAME' started successfully with image '$ECR_IMAGE' on port 8080."


# 컨테이너 시작 상태 확인 (디버깅용)
echo "DEBUG: Docker container command issued. Giving it some time to start up..."
sleep 5

echo "DEBUG: Current Docker processes:"
sudo docker ps -a

echo "DEBUG: Checking Docker container logs for initial startup messages..."
sudo docker logs "$CONTAINER_NAME" --tail 50

echo "--- ApplicationStart script finished ---"
