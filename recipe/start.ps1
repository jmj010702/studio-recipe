Write-Host "Spring Boot 'local' 프로필 환경 변수를 설정합니다..."

$env:MAIL_HOST = "smtp.naver.com"
$env:MAIL_PORT = "465"
$env:MAIL_USERNAME = "stay_on_track@naver.com"
$env:MAIL_PASSWORD = "KVRG8UGYM9ZJ"

$env:JWT_SECRET_KEY = "VGhpc0lzQUR1bW15U2VjcmV0S2V5Rm9yTG9jYWxUZXN0aW5nMTIzNDU2Nzg5MEFCQ0RFRg=="
$env:JWT_ACCESS_TOKEN_VALIDITY_IN_SECONDS = "3600"
$env:JWT_REFRESH_TOKEN_VALIDITY_IN_SECONDS = "86400"

$env:FRONT_URL = "http://localhost:5173"

$env:DRIVER_NAME = "org.mariadb.jdbc.Driver"
$env:DRIVER_URL = "jdbc:mariadb://localhost:3306/recipe_db"
$env:DRIVER_USER_NAME = "root"
$env:DRIVER_PASSWORD = "1234"

Write-Host "환경 변수 설정 완료. Spring Boot 서버를 'local' 프로필로 시작합니다..."

.\gradlew bootrun --args="--spring.profiles.active=local"