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
