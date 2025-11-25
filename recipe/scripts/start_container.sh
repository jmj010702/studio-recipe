#!/bin/bash
set -eux # 스크립트 실행 중 에러 발생 시 즉시 중단 및 실행된 명령 출력, 정의되지 않은 변수 사용 시 에러

echo "--- Starting Application: recipe-app-container ---"

# 1 ECR_IMAGE 변수 유효성 검사 및 디버깅
# 'unbound variable' 에러 발생 지점. 이 라인들이 에러를 해결합니다.
# CodeDeploy Arguments에서 전달받은 ECR_IMAGE가 비어있거나 정의되지 않았을 경우,
# 스크립트가 명확한 오류 메시지와 함께 중단되도록 합니다.
echo "DEBUG: ECR_IMAGE received from CodeDeploy arguments: $ECR_IMAGE"
: "${ECR_IMAGE:?ERROR: ECR_IMAGE environment variable is not set or is empty. It is required to run the Docker container.}"


# 2 Docker 로그인 및 이미지 다운로드
#  기존 ECR 로그인 및 pull 로직을 유지
echo "DEBUG: Logging in to ECR..."
aws ecr get-login-password --region ap-northeast-2 \
| sudo docker login --username AWS --password-stdin 516175389011.dkr.ecr.ap-northeast-2.amazonaws.com || true # 로그인 실패해도 중단되지 않도록 || true 추가 (재시도용)

echo "DEBUG: Pulling Docker image: $ECR_IMAGE"
sudo docker pull "$ECR_IMAGE"


# 3 Secrets Manager에서 환경 변수 가져오기
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
# 모든 환경 변수를 -e 옵션으로 컨테이너에 전달하기 위해 준비합니다.
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


# [수정/통합된 부분 4] 기존 컨테이너 정리 로직
# 기존 로직과 제가 제안했던 로직을 통합하여, 기존 컨테이너가 있다면
# 안전하게 중지하고 제거합니다. 불필요한 중복을 제거했습니다.
CONTAINER_NAME="recipe-app-container"
echo "DEBUG: Checking for existing container '$CONTAINER_NAME'..."
if sudo docker ps -a --format '{{.Names}}' | grep -q "^${CONTAINER_NAME}$"; then
  echo "DEBUG: Stopping and removing existing container: $CONTAINER_NAME"
  sudo docker stop "$CONTAINER_NAME" || true
  sudo docker rm "$CONTAINER_NAME" || true
else
  echo "DEBUG: No existing container '$CONTAINER_NAME' found. Skipping stop/remove."
fi


# 5 새 Docker 컨테이너 실행
# ENV_ARGS를 docker run 명령에 적용하고, ECR_IMAGE를 큰따옴표로 감쌌습니다.
# --health-cmd 옵션을 추가하여 Docker 자체의 컨테이너 헬스 체크 기능을 활용합니다.
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

echo "🚀 Docker container '$CONTAINER_NAME' started successfully with image '$ECR_IMAGE' on port 8080."
# ================================================================


# 6 컨테이너 시작 상태 확인 (디버깅용)
# 컨테이너가 실제로 백그라운드에서 실행되고 있는지 확인합니다.
# ================================================================
echo "DEBUG: Docker container command issued. Giving it some time to start up..."
sleep 5 # 컨테이너가 완전히 시작될 시간을 확보

echo "DEBUG: Current Docker processes:"
sudo docker ps -a

echo "DEBUG: Checking Docker container logs for initial startup messages..."
sudo docker logs "$CONTAINER_NAME" --tail 50 # 마지막 50줄 로그 출력
# ================================================================

echo "--- ApplicationStart script finished ---"
