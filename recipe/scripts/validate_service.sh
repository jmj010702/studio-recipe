#!/bin/bash
set -eux

# 애플리케이션 헬스 체크 URL
HEALTH_CHECK_URL="http://localhost:8080/health" # 실제 애플리케이션의 헬스 체크 엔드포인트로 변경
MAX_RETRIES=15
RETRY_INTERVAL=10 # seconds

echo "Waiting for application to become healthy at $HEALTH_CHECK_URL..."

for i in $(seq 1 $MAX_RETRIES); do
  if curl -s -f $HEALTH_CHECK_URL > /dev/null; then
    echo "Application is healthy!"
    exit 0
  fi
  echo "Application not healthy yet, retrying in $RETRY_INTERVAL seconds... (Attempt $i/$MAX_RETRIES)"
  sleep $RETRY_INTERVAL
done

echo "Application failed to become healthy within the timeout."
exit 1
