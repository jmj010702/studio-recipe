# --- 문제 해결: openjdk 이미지를 eclipse-temurin으로 변경 ---
FROM eclipse-temurin:17-jdk-alpine

# --- 기존 Dockerfile 내용 유지 ---
WORKDIR /app

RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} app.jar

# --- 'prod' 프로파일로 정확하게 변경 ---
ENTRYPOINT ["java","-jar","/app.jar", "--spring.profiles.active=prod"]
