pipeline {
    agent any
    
    // --- 1. Global Environment (Jenkinsfile_GlobalEnv 기반) ---
    environment {
        // 사용자의 실제 AWS 계정 ID와 ECR 리전을 기반으로 수정하세요.
        // 현재 설정: '516175389011'
        ECR_REGISTRY = '516175389011.dkr.ecr.ap-northeast-2.amazonaws.com' 
        IMAGE_NAME = 'recipe-app'
        // 백엔드 프로젝트 루트 디렉토리 설정 (Git 루트 아래 'recipe' 폴더를 가리킴)
        BACKEND_DIR = 'recipe'
    }

    stages {
        // --- 2. Code Checkout Stage ---
        stage('Checkout Code') {
            steps {
                echo "--- Git Repository에서 코드 체크아웃 ---"
                git branch: 'main', credentialsId: 'jenkins-github-credentials', url: 'https://github.com/YourRepo/recipe-app.git'
            }
        }
        
        // --- 3. Build Spring Boot App Stage (Jenkinsfile_BuildStage_Fix_No_Plugin.groovy 기반) ---
        stage('Build Spring Boot App (Gradle)') {
            steps {
                echo "--- Spring Boot 애플리케이션 빌드 시작 ---"
                // gradlew에 실행 권한 부여
                sh "chmod +x ${BACKEND_DIR}/gradlew"
                // 'recipe' 디렉토리에서 빌드 수행 (-p 옵션으로 프로젝트 경로 지정)
                sh "${BACKEND_DIR}/gradlew clean build -p ${BACKEND_DIR}"

                echo "--- JAR 파일 app.jar로 이름 변경 (Shell Command 사용) ---"
                sh """
                # 1. 빌드 디렉토리에서 가장 최근에 생성된 JAR 파일을 찾습니다.
                LATEST_JAR=$(ls -1t ${BACKEND_DIR}/build/libs/*.jar | head -n 1)

                if [ -z "\$LATEST_JAR" ]; then
                    echo "ERROR: 빌드 후 JAR 파일이 발견되지 않았습니다. 빌드 실패!"
                    exit 1
                fi

                TARGET_PATH="${BACKEND_DIR}/build/libs/app.jar"

                if [ "\$LATEST_JAR" != "\$TARGET_PATH" ]; then
                    # 파일 이름을 app.jar로 변경
                    mv "\$LATEST_JAR" "\$TARGET_PATH"
                    echo "JAR 파일 이름을 \${LATEST_JAR}에서 app.jar로 변경했습니다."
                else
                    echo "JAR 파일 이름이 이미 app.jar이므로 변경을 건너뜀니다."
                fi
                """
            }
        }

        // --- 4. Docker Build and Push Stage (새롭게 추가된 핵심 단계) ---
        stage('Docker Build & Push to ECR') {
            steps {
                echo "--- Docker 이미지 빌드 및 ECR 푸시 ---"
                script {
                    def imageTag = env.BUILD_NUMBER
                    def ecrImageUri = "${env.ECR_REGISTRY}/${env.IMAGE_NAME}:${imageTag}"
                    
                    // 1. ECR 로그인 (Jenkins IAM Role 권한 필요)
                    withAws(credentials: 'jenkins-aws-credentials', region: 'ap-northeast-2') {
                        sh "aws ecr get-login-password --region ap-northeast-2 | docker login --username AWS --password-stdin ${env.ECR_REGISTRY}"
                    }
                    
                    // 2. Docker 이미지 빌드 (Dockerfile은 recipe/ 폴더에 있다고 가정)
                    // 현재 WORKSPACE 루트에서 빌드 컨텍스트(.)를 사용하여 Dockerfile 경로를 지정합니다.
                    sh "docker build -t ${ecrImageUri} -f ${env.BACKEND_DIR}/Dockerfile ."
                    
                    // 3. ECR 푸시
                    sh "docker push ${ecrImageUri}"
                    echo "ECR Image Pushed: ${ecrImageUri}"
                }
            }
        }

        // --- 5. Deploy to CodeDeploy Stage (Jenkinsfile_DeployStage_Fix 기반) ---
        stage('Deploy to CodeDeploy') {
            steps {
                echo "--- CodeDeploy 배포 아티팩트 준비 및 배포 시작 ---"
                script {
                    def imageTag = env.BUILD_NUMBER
                    def ecrImageUri = "${env.ECR_REGISTRY}/${env.IMAGE_NAME}:${imageTag}"
                    
                    // 1. ECR 이미지 태그 파일을 WORKSPACE 루트에 생성 (CodeDeploy 스크립트에서 사용)
                    writeFile file: 'image_tag.txt', text: imageTag
                    echo "image_tag.txt 생성 완료: ${imageTag}"

                    // 2. CodeDeploy 아티팩트 복사 및 정리
                    // CodeDeploy는 WORKSPACE 루트의 파일을 사용해야 하므로, 필요한 파일 복사
                    sh "cp -r scripts ." // scripts/start_server.sh, scripts/stop_server.sh, scripts/validate_service.sh
                    sh "cp ${BACKEND_DIR}/build/libs/app.jar ."
                    // appspec.yml이 Git Root에 있다고 가정합니다. (appspec.yml도 필요함)
                    
                    // 3. 현재 작업 공간의 모든 필요한 파일을 zip으로 묶습니다.
                    sh "zip -r deployment.zip scripts app.jar appspec.yml image_tag.txt"
                }

                // 4. CodeDeploy 호출 (awsCodeDeploy 플러그인 또는 AWS CLI 사용)
                withAws(credentials: 'jenkins-aws-credentials', region: 'ap-northeast-2') {
                    // awsCodeDeploy 플러그인 사용 (Jenkins 설정 필요)
                    awsCodeDeploy appName: 'recipe-app-codedeploy', 
                                  deploymentGroupName: 'recipe-app-deployment-group', 
                                  file: 'deployment.zip', 
                                  s3Bucket: 'recipe-app-deploy-bucket', // 실제 S3 버킷 이름으로 변경
                                  s3Prefix: 'deploy-artifacts',
                                  wait: true // 배포 결과를 기다림
                }
            }
        }
    }
    
    // 워크스페이스 정리 (선택 사항이지만 디스크 부족 문제 해결에 도움 됨)
    post {
        always {
            echo "빌드 후 작업 공간을 정리합니다."
            cleanWs()
        }
    }
}
