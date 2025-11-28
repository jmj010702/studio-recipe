FROM eclipse-temurin:17-jdk-alpine

# 스프링 애플리케이션 실행을 위한 유저/그룹 생성
# 보안을 위해 root가 아닌 사용자로 실행
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# 작업 디렉토리 설정: JAR 파일이 여기에 복사되고 실행될 것입니다.
WORKDIR /app

COPY build/libs/app.jar /app/app.jar

# Spring 프로필은 start_container.sh에서 환경 변수로 전달받으므로 Dockerfile에 하드코딩하지 않습니다.
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
