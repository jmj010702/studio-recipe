# Docker
- Docker Compose 실행 명령어
```bash
$ docker compose up --build -d
```
- Docker Compose 종료
``` bash
$ docker compose down
# -v는 volumes까지 삭제
$ docker compose down -v
```

- Spring Boot Log 보기
```bash
$ docker compose logs -f backend #backend는 docker compose에 설정한 그룹(서비스) 이름, DNS 이름
```

- 사용 이유
 - Production과 동일한 환경으로 테스트를 진행하기 위함이고, 환경 일관성을 제공하여 내 컴퓨터에서는 잘 되는데 방지
 - Docker는 확장성(sacle out), 이식성, 격리성(각 앱의 라이브러리 버전 충돌), 마이크로 서비스에 각각의 서비스를 제공할 수 있는 점에서 향후 애플리케이션이 확장하는데 유연함을 제공하기 때문에 선택하였습니다.
 - Docker Compose 사용 이유는 DB 컨테이너를 build하고 Docker Network 연결..등 수동 작업을 docker compose로 자동화 해주고 service_healthy와 always를 설정하여 스프링부트의 DB 커넥션 문제로 인한 종료와  불필요한 재 반복을 막으면서 재 시작을 해줍니다. 이러한 편리한 기능 때문에 Docker Compose 기능을 사용하였습니다. 


# MariaDB
- Version: 10.11.14
- MariaDB 버전과 부가 정보 보기   
```sql
  SHOW VARIABLES LIKE '%VERSION%';
  ```

# Redis
- 향후 AWS 스케일아웃 환경에서 SSE는 정상 작동하지 않고 메일 서버 인증과 확장성을 위해 도입
- WSL Ubuntu 환경 설치 및 실행 가이드
```bash
# 설치
$ curl -fsSL https://packages.redis.io/gpg | sudo gpg --dearmor -o /usr/share/keyrings/redis-archive-keyring.gpg

$ echo "deb [signed-by=/usr/share/keyrings/redis-archive-keyring.gpg] https://packages.redis.io/deb $(lsb_release -cs) main" | sudo tee /etc/apt/sources.list.d/redis.list

$ sudo apt-get update
$ sudo apt-get install redis

# 실행
$ redis-server --daemonize yes

# Redis 연결
$ redis-cli

# 연결 테스트
127.0.0.1:6379> ping
PONG
```


# 트러블 슈팅
<details>
 <summary>리눅스 CSV Batch 작업</summary>
  - CSV 파일 인코딩 확인 후 변환
 
  ```bash
 #파일이 UTF-8로 인코딩 되어 있는지 확인
 file -i recipe_data_241226.csv

 #시스템 인코딩 변경
 LANG="ko_KR.UTF-8"

#윈도우에서는 CSV파일을 CRLF(\r\n) 줄 바꿈 사용, 리눅스는 LF(\n)만 사용하기에 줄바꿈 변환
 sudo apt install dos2unix
 dos2unix recipe_data_241226.csv

 #권한 변경
 chmod +r recipe_data_241226.csv
  ```


 - 컴포즈로 작성된 백엔드 로그 결과
 ```bash
 studio-recipe  | Caused by: java.sql.SQLSyntaxErrorException: (conn=4) Data too long for column 'ckg_mtrl_cn' at row 1
studio-recipe  |        at org.mariadb.jdbc.export.ExceptionFactory.createException(ExceptionFactory.java:289) ~[mariadb-java-client-3.5.5.jar!/:na]
studio-recipe  |        at org.mariadb.jdbc.export.ExceptionFactory.create(ExceptionFactory.java:378) ~[mariadb-java-client-3.5.5.jar!/:na]
studio-recipe  |        at org.mariadb.jdbc.message.ClientMessage.readPacket(ClientMessage.java:187) ~[mariadb-java-client-3.5.5.jar!/:na]
studio-recipe  |        at org.mariadb.jdbc.client.impl.StandardClient.readPacket(StandardClient.java:1380) ~[mariadb-java-client-3.5.5.jar!/:na]
studio-recipe  |        at org.mariadb.jdbc.client.impl.StandardClient.readResults(StandardClient.java:1319) ~[mariadb-java-client-3.5.5.jar!/:na]
studio-recipe  |        at org.mariadb.jdbc.client.impl.StandardClient.readResponse(StandardClient.java:1238) ~[mariadb-java-client-3.5.5.jar!/:na]
studio-recipe  |        at org.mariadb.jdbc.client.impl.StandardClient.execute(StandardClient.java:1162) ~[mariadb-java-client-3.5.5.jar!/:na]
studio-recipe  |        at org.mariadb.jdbc.ClientPreparedStatement.executeInternal(ClientPreparedStatement.java:87) ~[mariadb-java-client-3.5.5.jar!/:na]
studio-recipe  |        at org.mariadb.jdbc.ClientPreparedStatement.executeLargeUpdate(ClientPreparedStatement.java:307) ~[mariadb-java-client-3.5.5.jar!/:na]
studio-recipe  |        at org.mariadb.jdbc.ClientPreparedStatement.executeUpdate(ClientPreparedStatement.java:284) ~[mariadb-java-client-3.5.5.jar!/:na]
studio-recipe  |        at com.zaxxer.hikari.pool.ProxyPreparedStatement.executeUpdate(ProxyPreparedStatement.java:61) ~[HikariCP-6.3.2.jar!/:na]
studio-recipe  |        at com.zaxxer.hikari.pool.HikariProxyPreparedStatement.executeUpdate(HikariProxyPreparedStatement.java) ~[HikariCP-6.3.2.jar!/:na]
studio-recipe  |        at org.hibernate.engine.jdbc.internal.ResultSetReturnImpl.executeUpdate(ResultSetReturnImpl.java:194) ~[hibernate-core-6.6.26.Final.jar!/:6.6.26.Final]
studio-recipe  |        ... 161 common frames omitted
studio-recipe  |
studio-recipe  | 2025-10-11T08:46:48.384Z  INFO 1 --- [nio-8080-exec-1] o.s.batch.core.step.AbstractStep         : Step: [recipeDataMigrationStep] executed in 405ms
studio-recipe  | 2025-10-11T08:46:48.405Z  INFO 1 --- [nio-8080-exec-1] o.s.b.c.l.s.TaskExecutorJobLauncher      : Job: [SimpleJob: [name=recipeDataMigrationJob]] completed with the following parameters: [{'jobId':'{value=1760172407885, type=class java.lang.String, identifying=true}','run.id':'{value=1760172407886, type=class java.lang.Long, identifying=true}'}] and the following status: [FAILED] in 444ms
studio-recipe  | 2025-10-11T08:46:48.405Z  INFO 1 --- [nio-8080-exec-1] c.recipe.controller.BatchJobController   : Batch Job 'recipeDataMigrationJob' 실행 완료. Status: FAILED
 ```

- [Data too long for column] 최대 길이 문제
  ```java
  @Lob // Lob으로 되어 있었지만 해당 컬럼의 크기 문제 때문에 TEXT 명시적으로 적용
    @Column(name = "ckg_mtrl_cn", columnDefinition = "TEXT") 
  ```
  
</details>

<details>
 <summary>MailService & Redis 도입 후 테스트 서버 오류</summary>

 ## 주요 에러들
 ```bash
 studio-recipe   | org.springframework.beans.factory.UnsatisfiedDependencyException: Error creating bean with name 'authControllerImpl' defined in URL [jar:nested:/app.jar/!BOOT-INF/classes/!/com/recipe/controller/AuthControllerImpl.class]: Unsatisfied dependency expressed through constructor parameter 1: Error creating bean with name 'mailService': Injection of autowired dependencies failed
 
studio-recipe   |  Caused by: org.springframework.beans.factory.BeanCreationException: Error creating bean with name 'mailService': Injection of autowired dependencies failed
 ```

## 해결
- 초반에는 env 파일에서 환경 변수를 읽지 못하는 점이였다.
  초반에는 같은 방법으로 하였지만 해결이 되지 않아 리셋을 하였지만 구문 오류가 있었던 것 같다.
```bash
#environment에 환경 변수 추가
====================================
  SPRING_MAIL_HOST: ${MAIL_HOST}
  SPRING_MAIL_PORT: ${MAIL_PORT}
  MAIL_USERNAME: ${MAIL_USERNAME}
  MAIL_PASSWORD: ${MAIL_PASSWORD}
====================================
          or
  env_file:
  ./.env # .env 파일의 모든 변수가 컨테이너로 주입된다.
```

- 정상 동작
```bash
# ID 찾기
$ curl -X POST \
-H "Content-Type: application/json" \
-d '{
"email" : "dbsghks34@naver.com"
}' http://localhost:8080/studio-recipe/auth/send-verification
인증 번호 성공적으로 발송되었습니다.%

$ curl -X POST \
> -H "Content-Type: application/json" \
> -d '{
quote> "email" : "dbsghks34@naver.com",
quote> "verificationCode" : 243414,
quote> "purpose" : "FIND_ID"
quote> }' http://localhost:8080/studio-recipe/auth/verify-code
{"message":"이메일 인증이 성공했습니다.","token":"28046c58-1aa4-42e4-b86d-a8e12c663c07"}%

$ curl -X POST \
-H "Content-Type: application/json" \
-d '{
"token" : "28046c58-1aa4-42e4-b86d-a8e12c663c07"
}' http://localhost:8080/studio-recipe/auth/find-id
springboot!%

# 잘못된 토큰 신청 -> 토큰 만료 / 비밀번호 바꾸기 정상 동작                                                               
```

- 그 외
   - 우분투 상에서 설치된 Redis와 포트 번호 충돌 -> Docker Compose에 레디스 서비스 포트 번호 변경 6378:6379
 
</details>





11/21
회원탈퇴 및 비밀번호 변경 - MYpage에 구현
기능 구현에 맞춰서 JAVA코드 변경
레시피 작성시 정상적으로 DB에 저장이 되는지
검색시 레시피에 맞는 레시피 출력 및 작성 레시피가 출력되는지 확인인
-






