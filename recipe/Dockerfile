FROM eclipse-temurin:17-jdk-alpine

WORKDIR /app

# RUN addgroup -S spring && adduser -S spring -G spring
# USER spring:spring

COPY app.jar app.jar
# JAR 파일에 실행 권한 부여
RUN chmod +x /app/app.jar

# --- DEBUG: Docker 이미지 빌드 시 /app 디렉토리의 내용을 확인하는 임시 명령 ---
# 이 로그를 통해 app.jar 파일이 이미지에 제대로 복사되었는지 확인합니다.
RUN ls -al /app

ENTRYPOINT ["java", "-jar", "/app.jar", "--spring.profiles.active=prod"]
