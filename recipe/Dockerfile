FROM eclipse-temurin:17-jdk-alpine

WORKDIR /app

RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# 수정: JAR 파일을 빌드 컨텍스트 루트(recipe/)에서 바로 복사
COPY app.jar app.jar 

RUN ls -al /app

ENTRYPOINT ["java", "-jar", "/app.jar", "--spring.profiles.active=prod"]
