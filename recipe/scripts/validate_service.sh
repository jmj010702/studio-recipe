#!/bin/bash

APP_PORT=8080
APP_CONTEXT_PATH="/studio-recipe"

HEALTH_CHECK_URL="http://localhost:${APP_PORT}${APP_CONTEXT_PATH}/actuator/health"

MAX_RETRIES=15
RETRY_INTERVAL=10

echo "INFO: Waiting for application to become healthy at ${HEALTH_CHECK_URL}"

for i in $(seq 1 $MAX_RETRIES); do
    if curl -s -f ${HEALTH_CHECK_URL}; then
        echo "Application is healthy. Status: UP"
        exit 0
    else
        echo "Application not healthy yet at ${HEALTH_CHECK_URL}, retrying in ${RETRY_INTERVAL} seconds... (Attempt $i/$MAX_RETRIES)"
        sleep $RETRY_INTERVAL
    fi
done

echo "ERROR: Application failed to become healthy within the timeout."
exit 1