#!/bin/bash

SECRET_ID="${AWS_SECRETS_ID}"
AWS_REGION="${AWS_REGION}"

echo "Retrieving secrets from AWS Secrets Manager: ${SECRET_ID} in ${AWS_REGION}"

SECRET_STRING=$(aws secretsmanager get-secret-value --secret-id "${SECRET_ID}" --query SecretString --output text --region "${AWS_REGION}")

if [ -z "${SECRET_STRING}" ]; then
  echo "ERROR: Failed to retrieve secret string from Secrets Manager."
  exit 1
fi

DB_HOST=$(echo "${SECRET_STRING}" | jq -r '.DB_HOST')
DB_NAME=$(echo "${SECRET_STRING}" | jq -r '.DB_NAME')
DB_USERNAME=$(echo "${SECRET_STRING}" | jq -r '.DB_USERNAME')
DB_PASSWORD=$(echo "${SECRET_STRING}" | jq -r '.DB_PASSWORD')

MAIL_HOST=$(echo "${SECRET_STRING}" | jq -r '.MAIL_HOST')
MAIL_PORT=$(echo "${SECRET_STRING}" | jq -r '.MAIL_PORT')
MAIL_USERNAME=$(echo "${SECRET_STRING}" | jq -r '.MAIL_USERNAME')
MAIL_PASSWORD=$(echo "${SECRET_STRING}" | jq -r '.MAIL_PASSWORD')
// MAIL_FROM_ADDRESS=$(echo "${SECRET_STRING}" | jq -r '.MAIL_FROM_ADDRESS')

MY_APP_SECRET=$(echo "${SECRET_STRING}" | jq -r '.MY_APP_SECRET')

echo "Starting Docker container for ${ECR_IMAGE}"

sudo docker run -d \
  --name recipe-app-container \
  --network host \
  -e SPRING_DATASOURCE_URL="jdbc:mariadb://${DB_HOST}:3306/${DB_NAME}?useUnicode=true&characterEncoding=UTF-8&serverTimezone=UTC" \
  -e SPRING_DATASOURCE_USERNAME="${DB_USERNAME}" \
  -e SPRING_DATASOURCE_PASSWORD="${DB_PASSWORD}" \
  -e SPRING_MAIL_HOST="${MAIL_HOST}" \
  -e SPRING_MAIL_PORT="${MAIL_PORT}" \
  -e SPRING_MAIL_USERNAME="${MAIL_USERNAME}" \
  -e SPRING_MAIL_PASSWORD="${MAIL_PASSWORD}" \
  -v /var/lib/docker/data:/app/data \
  "${ECR_IMAGE}"

if [ $? -ne 0 ]; then
    echo "ERROR: Failed to run Docker container."
    exit 1
fi
echo "Docker container recipe-app-container started successfully with image ${ECR_IMAGE}."
