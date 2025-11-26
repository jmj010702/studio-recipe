FROM eclipse-temurin:17-jdk-alpine

# 스프링 애플리케이션 실행을 위한 유저/그룹 생성
# 보안을 위해 root가 아닌 사용자로 실행
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# 작업 디렉토리 설정: JAR 파일이 여기에 복사되고 실행될 것입니다.
WORKDIR /app

# Jenkinsfile에서 이름을 'app.jar'로 변경하여 고정된 파일명을 복사합니다.
# 빌드 컨텍스트가 'recipe' 디렉토리이므로 'build/libs/app.jar'는 'recipe/build/libs/app.jar'를 의미합니다.
ARG JAR_FILE=build/libs/app.jar # 이 ARG는 참조용이며, COPY에서 직접 경로 사용
COPY ${JAR_FILE} /app/app.jar # <-- 이 부분 수정: 'build/libs/app.jar'를 '/app/app.jar'로 복사

# Spring 프로필은 start_container.sh에서 환경 변수로 전달받으므로 Dockerfile에 하드코딩하지 않습니다.
ENTRYPOINT ["java", "-jar", "/app/app.jar"] # <-- 이 부분 수정: JAR 실행 경로
