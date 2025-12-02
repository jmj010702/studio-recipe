#!/bin/bash

# ValidateService 스크립트는 애플리케이션이 성공적으로 시작되고
# 헬스 체크 엔드포인트에 응답하는지 확인합니다.

# --- 1. 상수 정의 ---
HEALTH_CHECK_URL="http://localhost:8080/actuator/health" # Spring Boot Actuator Health Check Endpoint
MAX_RETRIES=15                                            # 최대 재시도 횟수 (15 * 10초 = 150초 = 2분 30초)
RETRY_INTERVAL=10                                         # 재시도 간격 (초)

echo "INFO: Waiting for application to become healthy at ${HEALTH_CHECK_URL}"

for i in $(seq 1 $MAX_RETRIES); do
    HTTP_STATUS=$(curl -s -o /dev/null -w "%{http_code}" ${HEALTH_CHECK_URL})

    if [ "$HTTP_STATUS" -eq 200 ]; then
        echo "INFO: Application is healthy (HTTP Status 200)."
        exit 0 # 성공적으로 헬스 체크 통과
    else
        echo "Application not healthy yet at ${HEALTH_CHECK_URL} (HTTP Status: ${HTTP_STATUS}), retrying in ${RETRY_INTERVAL} seconds... (Attempt $i/$MAX_RETRIES)"
        sleep $RETRY_INTERVAL
    fi
done

echo "ERROR: Application failed to become healthy within the timeout."
exit 1 # 최대 재시도 횟수 초과, 실패
