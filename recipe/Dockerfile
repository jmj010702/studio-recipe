FROM eclipse-temurin:17-jdk-alpine

WORKDIR /app

RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

COPY app.jar app.jar 

RUN ls -al /app

ENTRYPOINT ["java", "-jar", "/app.jar", "--spring.profiles.active=prod"]
