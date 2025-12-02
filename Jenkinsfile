#!/bin/bash

# ... (스크립트 상단 내용 및 jq 설치 확인 부분 생략 - 이전 버전 그대로 두시면 됩니다) ...

# --- 1. 상수 정의 (start_container.sh 스크립트 내에서 사용될 변수들) ---
CONTAINER_NAME="recipe-app-container"
ECR_REGION="ap-northeast-2" # ECR 리전 지정
SECRET_ID="recipe-app-secrets" # AWS Secrets Manager에서 사용할 Secret ID

# ... (2. ECR 이미지 URI 추출 부분 생략 - 이전 버전 그대로 두시면 됩니다) ...

# --- 3. Secrets Manager에서 비밀값 가져오기 및 파싱 ---
echo "Fetching secrets from AWS Secrets Manager: ${SECRET_ID} in region ${ECR_REGION}"

# aws secretsmanager 명령을 실행하고 표준 출력과 에러를 모두 'SECRET_JSON' 변수에 저장합니다.
# '|| true'를 사용해 aws 명령 자체의 실패 시에도 스크립트가 바로 종료되지 않고,
# 에러 메시지가 'SECRET_JSON'에 담기도록 합니다. (이후 단계에서 에러 메시지를 출력하기 위함)
set -o pipefail # 파이프라인의 어떤 명령이든 실패하면 전체 파이프라인이 실패하도록 설정
SECRET_JSON=$(aws secretsmanager get-secret-value --secret-id "${SECRET_ID}" --region "${ECR_REGION}" --query SecretString --output text 2>&1 || true)

# AWS CLI 명령 실행 자체의 성공/실패를 먼저 검증합니다.
# 만약 'SECRET_JSON'에 "An error occurred"와 같은 AWS CLI 에러 메시지가 포함되어 있다면 실패로 처리합니다.
if echo "${SECRET_JSON}" | grep -q "An error occurred"; then
    echo "ERROR: AWS CLI failed to retrieve secrets from Secrets Manager for '${SECRET_ID}'."
    echo "       Please check IAM permissions of this EC2 instance, network connectivity, and secret ID."
    echo "AWS CLI Error Output: ${SECRET_JSON}" # 상세 에러 메시지 출력
    exit 1
fi

# SecretString이 비어있거나 'null', 'None'인 경우 처리합니다.
# Secrets Manager에 SecretString이 없으면 "None"으로 출력되거나, 아무것도 출력되지 않을 수 있습니다.
if [ -z "${SECRET_JSON}" ] || [ "${SECRET_JSON}" == "null" ] || [ "${SECRET_JSON}" == "None" ]; then
    echo "ERROR: SecretString is empty, null, or not found in Secrets Manager for ID '${SECRET_ID}'."
    echo "       This could mean the secret exists but has no SecretString value, or the value is invalid."
    exit 1
fi

# 이제 SECRET_JSON이 유효한 JSON 형식인지 간단히 검증합니다.
# 'jq -e .'는 유효한 JSON이면 0을, 아니면 1을 반환합니다.
if ! echo "${SECRET_JSON}" | jq -e . > /dev/null 2>&1; then
    echo "ERROR: Retrieved SecretString is not a valid JSON format."
    echo "       Please ensure the value stored in Secrets Manager for '${SECRET_ID}' is a valid JSON."
    echo "Invalid SecretString Content: ${SECRET_JSON}"
    exit 1
fi

# jq를 사용하여 JSON에서 필요한 값 추출
# 이 키 이름들은 Secrets Manager의 JSON에 저장된 키 이름과 정확히 일치해야 합니다.
# (이전 버전과 동일)
DB_USERNAME=$(echo "${SECRET_JSON}" | jq -r '.DB_USERNAME')
DB_PASSWORD=$(echo "${SECRET_JSON}" | jq -r '.DB_PASSWORD')
DB_HOST=$(echo "${SECRET_JSON}" | jq -r '.DB_HOST')
DB_NAME=$(echo "${SECRET_JSON}" | jq -r '.DB_NAME')

MAIL_USERNAME=$(echo "${SECRET_JSON}" | jq -r '.MAIL_USERNAME')
MAIL_PASSWORD=$(echo "${SECRET_JSON}" | jq -r '.MAIL_PASSWORD')
MAIL_HOST=$(echo "${SECRET_JSON}" | jq -r '.MAIL_HOST')
MAIL_PORT=$(echo "${SECRET_JSON}" | jq -r '.MAIL_PORT')

JWT_SECRET=$(echo "${SECRET_JSON}" | jq -r '.MY_APP_SECRET')

# 환경 변수 검증 (이전 버전과 동일)
if [ -z "${DB_USERNAME}" ] || [ -z "${DB_PASSWORD}" ] || [ -z "${DB_HOST}" ] || [ -z "${DB_NAME}" ] || \
   [ -z "${MAIL_USERNAME}" ] || [ -z "${MAIL_PASSWORD}" ] || [ -z "${MAIL_HOST}" ] || [ -z "${MAIL_PORT}" ] || \
   [ -z "${JWT_SECRET}" ]; then
    echo "ERROR: One or more required secret values could not be extracted or are empty from Secrets Manager."
    echo "DEBUG INFO: DB_USERNAME=[${DB_USERNAME}], MAIL_USERNAME=[${MAIL_USERNAME}], JWT_SECRET=[${JWT_SECRET}]]"
    echo "Please ensure all required keys exist and have non-empty values in Secrets Manager JSON."
    exit 1
fi

echo "Secrets fetched and parsed successfully."
echo "DEBUG INFO: DB_HOST=[${DB_HOST}], MAIL_HOST=[${MAIL_HOST}], JWT_SECRET has value." # 실제 값 노출 방지

# ... (4. ECR 로그인 및 5. Docker 컨테이너 실행 부분 생략 - 이전 버전 그대로 두시면 됩니다) ...
