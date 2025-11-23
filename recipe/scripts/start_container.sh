!/bin/bash
set -eux # 에러 발생 시 즉시 중단, 실행 명령어 출력

# 1. AppSpec.yml의 arguments로부터 ECR 이미지 이름과 태그를 받습니다.
#    이 변수명(ECR_IMAGE)은 appspec.yml에 정의된 arguments 이름과 동일해야 합니다.
#    예: hooks: ApplicationStart: arguments: - ECR_IMAGE="your_ecr_image:tag"
ECR_IMAGE=$ECR_IMAGE # CodeDeploy는 hooks arguments를 환경 변수처럼 주입

echo "🐳 Deploying Docker Image: $ECR_IMAGE"

# 2. ECR (Elastic Container Registry)에 로그인합니다.
#    ap-northeast-2 리전, AWS 계정 ID는 실제 사용자의 계정 ID로 대체되어야 합니다.
#    (Jenkins 빌드 로그에서 516175389011 같은 숫자를 확인)
aws ecr get-login-password --region ap-northeast-2 \
| docker login --username AWS --password-stdin 516175389011.dkr.ecr.ap-northeast-2.amazonaws.com

# 3. ECR에서 Docker 이미지를 다운로드(pull)합니다.
docker pull $ECR_IMAGE

# 4. Secrets Manager에서 필요한 비밀(Secret) 값들을 가져와 환경 변수로 준비합니다.
#    'recipe-app-secrets'는 사용하시는 Secrets Manager Secret의 이름입니다.
SECRET_STRING=$(aws secretsmanager get-secret-value --secret-id recipe-app-secrets --query SecretString --output text --region ap-northeast-2)

# Secrets Manager Secret의 JSON 구조에 맞춰 환경 변수들을 파싱합니다.
# 'jq' 명령어가 필요하며, AMI 생성 시 apt-get install -y jq로 설치되어 있어야 합니다.
DATABASE_HOST=$(echo "$SECRET_STRING" | jq -r '.DATABASE_HOST')
DATABASE_PORT=$(echo "$SECRET_STRING" | jq -r '.DATABASE_PORT')
DATABASE_USER=$(echo "$SECRET_STRING" | jq -r '.DATABASE_USER')
DATABASE_PASSWORD=$(echo "$SECRET_STRING" | jq -r '.DATABASE_PASSWORD') # 비밀번호도 반드시 필요!
REDIS_HOST=$(echo "$SECRET_STRING" | jq -r '.REDIS_HOST')
REDIS_PORT=$(echo "$SECRET_STRING" | jq -r '.REDIS_PORT')
# 다른 필요한 환경 변수가 있다면 여기에 추가: MY_API_KEY=$(echo "$SECRET_STRING" | jq -r '.MY_API_KEY')

# 5. Docker 컨테이너에 전달할 환경 변수들을 구성합니다.
#    이들은 Spring Boot 애플리케이션의 'application.yml'이나 'application.properties' 설정과 일치해야 합니다.
#    예시: SPRING_DATASOURCE_URL, SPRING_DATASOURCE_USERNAME 등
ENV_ARGS="-e SPRING_DATASOURCE_URL='jdbc:mysql://${DATABASE_HOST}:${DATABASE_PORT}/recipe_db?useSSL=false&allowPublicKeyRetrieval=true'"
ENV_ARGS+=" -e SPRING_DATASOURCE_USERNAME=${DATABASE_USER}"
ENV_ARGS+=" -e SPRING_DATASOURCE_PASSWORD=${DATABASE_PASSWORD}"
ENV_ARGS+=" -e SPRING_REDIS_HOST=${REDIS_HOST}" # Spring Boot의 Redis 호스트 설정명에 맞게
ENV_ARGS+=" -e SPRING_REDIS_PORT=${REDIS_PORT}"
ENV_ARGS+=" -e SPRING_PROFILES_ACTIVE=prod" # 배포 환경에 맞는 프로파일 설정 (예: prod, dev, qa)
# 추가적인 환경 변수가 있다면 여기에 계속 추가합니다. (예: ENV_ARGS+=" -e MY_APP_CONFIG=${MY_APP_VALUE}")

# 6. 이전 Docker 컨테이너를 중지하고 제거합니다. (만약 이전에 실행 중이었다면)
#    이 부분은 `ApplicationStop` 훅에서 처리할 수도 있습니다.
CONTAINER_NAME="recipe-app-container"
if docker ps -a | grep -q $CONTAINER_NAME; then
  echo "Stopping and removing existing container: $CONTAINER_NAME"
  docker stop $CONTAINER_NAME
  docker rm $CONTAINER_NAME
fi

# 7. 새 Docker 컨테이너를 실행합니다.
#    -d: 백그라운드에서 실행
#    --name: 컨테이너 이름 지정
#    --restart always: 컨테이너가 종료되면 항상 다시 시작 (EC2 재부팅 등)
#    -p 8080:8080: 호스트의 8080 포트를 컨테이너의 8080 포트에 매핑
docker run -d \
  --name $CONTAINER_NAME \
  --restart always \
  -p 8080:8080 \
  $ENV_ARGS \
  $ECR_IMAGE

echo "🚀 Docker container '$CONTAINER_NAME' started successfully with image '$ECR_IMAGE' on port 8080."
