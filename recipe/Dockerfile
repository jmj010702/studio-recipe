FROM eclipse-temurin:17-jdk-alpine

# 애플리케이션의 작업 디렉토리
WORKDIR /app

# 보안 강화를 위해 비-루트 사용자 'spring' 생성
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# 빌드된 JAR 파일을 /app 디렉토리로 명시적으로 복사
# Jenkinsfile에서 'recipe/build/libs/app.jar'로 파일명을 변경하므로,
# Docker build context (Jenkinsfile의 `recipe`) 내에서 해당 경로를 직접 지정
COPY build/libs/app.jar app.jar

ENTRYPOINT ["java", "-jar", "/app.jar", "--spring.profiles.active=prod"]
