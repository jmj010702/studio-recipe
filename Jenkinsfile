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
                    // gradlew 스크립트에 실행 권한 부여 (Permission denied 오류 해결)
                    sh "chmod +x gradlew"
                    // Spring Boot 애플리케이션 빌드
                    sh "./gradlew clean build"
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
                    
                    // Docker 이미지 빌드 (Dockerfile은 프로젝트 루트에 있다고 가정)
                    sh """
                    docker build -t ${ECR_REPOSITORY_URI}:${env.BUILD_NUMBER} -f recipe/Dockerfile .
                    """
                    
                    // Docker 이미지를 ECR에 푸시
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

                    // 1. appspec.yml 파일 읽기
                    def appspecContent = readFile('appspec.yml')
                    
                    // 2. ECR_IMAGE 플레이스홀더 치환 (젠킨스 빌드 번호를 이용하여 이미지 태그 완성)
                    appspecContent = appspecContent.replace('${BUILD_NUMBER}', env.BUILD_NUMBER)
                    
                    // 3. 수정된 appspec.yml 내용을 원본 파일에 다시 쓰기
                    // 이 파일은 곧 생성될 deployment.zip에 포함
                    writeFile(file: 'appspec.yml', text: appspecContent)
                    
                    // 4. CodeDeploy 배포 번들 (deployment.zip) 생성
                    // appspec.yml, scripts 디렉토리, build/libs/*.jar 파일을 포함
                    sh """
                    zip -r deployment.zip appspec.yml scripts build/libs/
                    """
                    
                    // 5. 배포 번들을 S3에 업로드
                    sh """
                    aws s3 cp deployment.zip s3://${S3_BUCKET}/recipe-app/${env.BUILD_NUMBER}.zip
                    """
                    
                    // 6. CodeDeploy 배포 생성
                    // aws deploy create-deployment 명령은 Jenkins 환경에서 AWS CLI를 통해 실행
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
            cleanWs() // 워크스페이스 정리
        }
        success {
            echo "CI/CD Pipeline finished successfully for build ${env.BUILD_NUMBER}."
        }
        failure {
            echo "CI/CD Pipeline failed for build ${env.BUILD_NUMBER}. Check Jenkins logs and AWS CodeDeploy console for details."
        }
    }
}
