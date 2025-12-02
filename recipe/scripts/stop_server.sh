#!/bin/bash
# CodeDeploy Hook: BeforeInstall / ApplicationStop

# 기존 컨테이너가 실행 중인지 확인하고 중지 및 제거
if docker ps -a | grep jenkins_app; then
    echo "Stopping and removing existing jenkins_app container..."
    docker stop jenkins_app
    docker rm jenkins_app
else
    echo "No existing jenkins_app container found."
fi
