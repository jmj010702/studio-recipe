pipeline {
    agent any // Jenkins 에이전트가 어떤 머신에서든 실행될 수 있도록 설정

    environment {
        AWS_REGION = 'ap-northeast-2'
        
        ECR_REPOSITORY_URI = '516175389011.dkr.ecr.ap-northeast-2.amazonaws.com/recipe-app'
        
        // S3 아티팩트 버킷 이름
        S3_BUCKET = 'recipe-app-codedeploy-artifacts-516175389011'
        
        // CodeDeploy 애플리케이션 및 배포 그룹 이름
        CODEDEPLOY_APPLICATION = 'recipe-app-codedeploy'
        CODEDEPLOY_DEPLOYMENT_GROUP = 'recipe-app-webserver-tg'
        
        // 백엔드 서비스의 Dockerfile, gradlew, JAR 파일이 위치한 서브 디렉토리 이름
        BACKEND_DIR = 'recipe'
    }

    stages {
        stage('Checkout') {
            steps {
                echo "--- Checking out source code ---"
                git branch: 'main', credentialsId: 'JG', url: 'https://github.com/stayonasDev/studio-recipe.git'
            }
        }

        stage('Build Spring Boot Application') {
            steps {
                echo "--- Building Spring Boot application ---"
                script {
                    // 백엔드 디렉토리(recipe) 안의 gradlew 스크립트에 실행 권한 부여
                    sh "chmod +x ${BACKEND_DIR}/gradlew"

                    // 백엔드 디렉토리(recipe) 안의 gradlew를 사용하여 애플리케이션 빌드
                    // '-p' 옵션으로 빌드할 프로젝트의 루트 경로를 지정합니다.
                    sh "${BACKEND_DIR}/gradlew clean build -p ${BACKEND_DIR}"

                    // 빌드된 JAR 파일을 찾아 'app.jar'로 이름을 변경하는 로직
                    // 'COPY failed: file not found' 오류를 해결하고 Dockerfile을 간소화합니다.
                    echo "--- Renaming JAR file to app.jar ---"
                    // ${BACKEND_DIR}/build/libs 디렉토리에서 *.jar 파일을 찾습니다. (예: recipe/build/libs/*.jar)
                    def jarFiles = findFiles(glob: "${BACKEND_DIR}/build/libs/*.jar")
                    
                    if (jarFiles.length == 0) {
                        error "Error: No JAR file found in ${BACKEND_DIR}/build/libs after build! Build failed."
                    }
                    def originalJarPath = jarFiles[0].path // 찾은 첫 번째 JAR 파일의 전체 경로
                    def renamedJarPath = "${BACKEND_DIR}/build/libs/app.jar" // 고정된 app.jar의 경로
                    
                    // 찾은 JAR 파일의 이름을 'app.jar'로 변경합니다.
                    sh "mv ${originalJarPath} ${renamedJarPath}"
                    echo "Renamed '${originalJarPath}' to '${renamedJarPath}' successfully."
                    // =================================================================
                }
            }
        }

        stage('Docker Build & Push to ECR') {
            steps {
                echo "--- Building Docker image and pushing to ECR ---"
                script {
                    // ECR에 로그인
                    sh """
                    aws ecr get-login-password --region ${AWS_REGION} | docker login --username AWS --password-stdin ${ECR_REPOSITORY_URI}
                    """
                    
                    // Docker 이미지 빌드 명령:
                    // -t: 이미지 태그 지정
                    // -f ${BACKEND_DIR}/Dockerfile: Dockerfile의 정확한 경로 지정 (예: recipe/Dockerfile)
                    // 마지막의 ${BACKEND_DIR}: 빌드 컨텍스트를 'recipe' 디렉토리로 설정
                    // 이전에 발생한 'Dockerfile: no such file or directory'와 'COPY failed' 오류를 해결합니다.
                    sh """
                    docker build -t ${ECR_REPOSITORY_URI}:${env.BUILD_NUMBER} -f ${BACKEND_DIR}/Dockerfile ${BACKEND_DIR}
                    """
                    
                    // 빌드된 Docker 이미지를 ECR에 푸시
                    sh """
                    docker push ${ECR_REPOSITORY_URI}:${env.BUILD_NUMBER}
                    """
                }
            }
        }

        stage('Prepare and Deploy to CodeDeploy') {
            steps {
                script {
                    echo "--- Preparing appspec.yml and creating CodeDeploy deployment ---"

                    // 1. appspec.yml 파일 내용 읽기
                    def appspecContent = readFile('appspec.yml')
                    
                    // 2. ECR_IMAGE 플레이스홀더 치환
                    // appspec.yml 내부의 '${BUILD_NUMBER}'를 Jenkins의 실제 빌드 번호로 교체
                    appspecContent = appspecContent.replace('${BUILD_NUMBER}', env.BUILD_NUMBER)
                    
                    // 3. 수정된 appspec.yml 내용을 원본 파일에 다시 쓰기
                    // 이 파일은 곧 생성될 deployment.zip에 포함
                    writeFile(file: 'appspec.yml', text: appspecContent)
                    
                    // CodeDeploy 배포 번들 (deployment.zip) 생성
                    sh """
                    zip -r deployment.zip appspec.yml scripts ${BACKEND_DIR}/build/libs/app.jar
                    """
                    
                    // 5. 생성된 배포 번들 ZIP 파일을 S3 버킷에 업로드
                    sh """
                    aws s3 cp deployment.zip s3://${S3_BUCKET}/recipe-app/${env.BUILD_NUMBER}.zip
                    """
                    
                    // 6. AWS CodeDeploy API를 호출하여 배포 시작
                    sh """
                    aws deploy create-deployment \\
                      --application-name ${CODEDEPLOY_APPLICATION} \\
                      --deployment-group-name ${CODEDEPLOY_DEPLOYMENT_GROUP} \\
                      --deployment-config-name CodeDeployDefault.OneAtATime \\
                      --description "Blue/Green Deployment triggered by Jenkins build ${env.BUILD_NUMBER}" \\
                      --s3-location bucket=${S3_BUCKET},key=recipe-app/${env.BUILD_NUMBER}.zip,bundleType=zip \\
                      --region ${AWS_REGION}
                    """
                }
            }
        }
    }

    post {
        always { // 빌드 성공/실패 여부와 상관없이 항상 실행
            cleanWs() // Jenkins 워크스페이스 정리 (다음 빌드를 위해 깨끗하게 유지)
        }
        success {
            echo "CI/CD Pipeline finished successfully for build ${env.BUILD_NUMBER}."
        }
        failure {
            echo "CI/CD Pipeline failed for build ${env.BUILD_NUMBER}. Check Jenkins logs and AWS CodeDeploy console for details."
        }
    }
}
