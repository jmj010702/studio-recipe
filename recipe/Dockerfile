FROM eclipse-temurin:17-jdk-alpine

RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

WORKDIR /app

COPY build/libs/app.jar /app/app.jar

ENTRYPOINT ["java","-jar","/app.jar", "--spring.profiles.active=prod"] 
