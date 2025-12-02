#!/bin/bash
set -e

echo "--- validate_service.sh script initiated ---"
echo "Running as user: $(whoami)"
echo "Current directory: $(pwd)"

APP_PORT=8080
CONTEXT_PATH="/studio-recipe"
HEALTH_ENDPOINT="/actuator/health"
HEALTH_URL="http://localhost:${APP_PORT}${CONTEXT_PATH}${HEALTH_ENDPOINT}"

MAX_ATTEMPTS=30
SLEEP_SECONDS=5

echo "INFO: Waiting for application to become healthy at ${HEALTH_URL}"

attempt=1
while [ $attempt -le $MAX_ATTEMPTS ]; do
  echo "INFO: Health check attempt ${attempt}/${MAX_ATTEMPTS}..."

  # HTTP 상태코드만 추출
  HTTP_CODE=$(curl -s -o /dev/null -w "%{http_code}" "${HEALTH_URL}" || echo "000")
  echo "INFO: Health check HTTP status: ${HTTP_CODE}"

  if [ "$HTTP_CODE" = "200" ]; then
    echo "SUCCESS: Application is healthy."
    exit 0
  fi

  echo "WARN: Application not healthy yet. Retrying in ${SLEEP_SECONDS} seconds..."
  sleep ${SLEEP_SECONDS}
  attempt=$((attempt + 1))
done

echo "ERROR: Application did not become healthy after ${MAX_ATTEMPTS} attempts. Failing deployment."
exit 7
