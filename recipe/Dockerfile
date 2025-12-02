FROM eclipse-temurin:17-jdk-alpine

WORKDIR /app

RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# docker build 명령이 실행되는 context (recipe/ 디렉토리)를 기준으로 app.jar를 찾음
COPY app.jar app.jar

# DEBUG: Docker 이미지 빌드 시 /app 디렉토리의 내용을 확인
RUN ls -al /app

ENTRYPOINT ["java", "-jar", "/app.jar", "--spring.profiles.active=prod"]
