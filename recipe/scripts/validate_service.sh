#!/bin/bash

# --- 스크립트 실행 시작 알림 ---
echo "--- validate_service.sh script initiated ---"
echo "Running as user: $(whoami)"
echo "Current directory: $(pwd)"

set -eo pipefail

HEALTH_CHECK_URL="http://localhost:8080/actuator/health"
MAX_ATTEMPTS=15
WAIT_SECONDS=10
CONTAINER_NAME="recipe-app-container"

echo "INFO: Waiting for application to become healthy at ${HEALTH_CHECK_URL}"

for i in $(seq 1 $MAX_ATTEMPTS); do
    STATUS_CODE=$(curl -s -o /dev/null -w "%{http_code}" "${HEALTH_CHECK_URL}")
    
    # curl 명령어가 실패할 경우 (예: Connection refused) HTTP 코드 000을 반환하도록 처리
    if [ $? -ne 0 ] || [ "${STATUS_CODE}" -eq 000 ]; then
        echo "Application not healthy yet at ${HEALTH_CHECK_URL} (HTTP Status: ${STATUS_CODE}), retrying in ${WAIT_SECONDS} seconds... (Attempt ${i}/${MAX_ATTEMPTS})"
    else
        # 2xx 상태 코드를 받으면 정상으로 간주
        if [[ "${STATUS_CODE}" -ge 200 && "${STATUS_CODE}" -le 299 ]]; then
            echo "Application is healthy! (HTTP Status: ${STATUS_CODE})"
            exit 0
        else
            echo "Application returned non-2xx status code: ${STATUS_CODE}, retrying in ${WAIT_SECONDS} seconds... (Attempt ${i}/${MAX_ATTEMPTS})"
        fi
    fi
    sleep "${WAIT_SECONDS}"
done

echo "ERROR: Application failed to become healthy within the timeout. Checking Docker container status and logs for ${CONTAINER_NAME}."
sudo docker ps -a --filter "name=${CONTAINER_NAME}"
sudo docker logs "${CONTAINER_NAME}"
exit 1
