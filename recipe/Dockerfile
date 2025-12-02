FROM eclipse-temurin:17-jdk-alpine

WORKDIR /app

ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} ./

RUN chmod +x /app/app.jar
RUN ls -al /app

ENTRYPOINT ["java", "-jar", "/app/app.jar", "--spring.profiles.active=prod"]
