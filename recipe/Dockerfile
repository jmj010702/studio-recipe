FROM eclipse-temurin:17-jdk-alpine

WORKDIR /app

# 보안 강화를 위해 비-루트 사용자 'spring' 생성
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

COPY build/libs/app.jar app.jar

RUN ls -al /app 

ENTRYPOINT ["java", "-jar", "/app.jar", "--spring.profiles.active=prod"]
