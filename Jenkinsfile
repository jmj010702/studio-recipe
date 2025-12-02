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
                //script {
                    //checkout([$class: 'GitSCM', branches: [[name: 'main']],
                            //extensions: [],
                            //userRemoteConfigs: [[credentialsId: 'JG', url: 'https://github.com/stayonasDev/studio-recipe.git']]])
            }
        }

        stage('Build Spring Boot Application') {
            steps {
                echo "--- Building Spring Boot application ---"
                script {
                    sh "chmod +x ${BACKEND_DIR}/gradlew"
                    sh "${BACKEND_DIR}/gradlew clean build -p ${BACKEND_DIR}"

                    echo "--- Renaming JAR file to app.jar ---"
                    def jarFiles = findFiles(glob: "${BACKEND_DIR}/build/libs/*.jar")
                    
                    if (jarFiles.length == 0) {
                        error "Error: No JAR file found in ${BACKEND_DIR}/build/libs after build! Build failed."
                    }
                    def originalJarPath = jarFiles[0].path // 찾은 첫 번째 JAR 파일의 전체 경로
                    def targetJarName = "app.jar" // 목표로 하는 파일 이름
                    def targetJarPath = "${BACKEND_DIR}/build/libs/${targetJarName}" // 목표로 하는 파일의 전체 경로
                    
                    // 파일 이름만 추출
                    def currentJarFileName = originalJarPath.tokenize('/')[-1]
                    
                    if (currentJarFileName != targetJarName) {
                        sh "mv ${originalJarPath} ${targetJarPath}"
                        echo "Renamed '${originalJarPath}' to '${targetJarPath}' successfully."
                    } else {
                        echo "JAR file is already named '${targetJarName}'. No rename needed."
                    }
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

                    // appspec.yml 치환 및 ECR_IMAGE_VALUE.txt 생성 (생략: 기존 코드와 동일)
                    def appspecContent = readFile('appspec.yml')
                    appspecContent = appspecContent.replace('${BUILD_NUMBER}', env.BUILD_NUMBER)
                    writeFile(file: 'appspec.yml', text: appspecContent)
                    
                    def ecrImageFullPath = "${ECR_REPOSITORY_URI}:${env.BUILD_NUMBER}"
                    echo "Generating ECR_IMAGE_VALUE.txt with: ${ecrImageFullPath}"
                    writeFile(file: 'ECR_IMAGE_VALUE.txt', text: ecrImageFullPath)

                    echo "DEBUG: Copying deployment artifacts to Jenkins workspace root for zipping..."
                    sh "cp -r ${BACKEND_DIR}/scripts ."
                    sh "test -d scripts/ && test -f scripts/clean_old_images.sh || error 'scripts directory or clean_old_images.sh not found after copy!'"
                    sh "cp ${BACKEND_DIR}/build/libs/app.jar ."
                    sh "test -f app.jar || error 'app.jar not found after copy!'"
                    echo "DEBUG: All artifacts copied to Jenkins workspace root."

                    // CodeDeploy 배포 번들 (deployment.zip) 생성
                    sh """
                    zip -r deployment.zip appspec.yml scripts app.jar ECR_IMAGE_VALUE.txt
                    """
                    
                    // S3 업로드
                    sh """
                    aws s3 cp deployment.zip s3://${S3_BUCKET}/recipe-app/${env.BUILD_NUMBER}.zip
                    """
                    
                    // --- 여기서부터 수정된 코드 시작 (반복 오류 해결) ---
                    echo "--- Checking for and stopping any active CodeDeploy deployments ---"
                    def activeDeploymentIds = sh(returnStdout: true, script: """
                    aws deploy list-deployments \\
                                                 --application-name ${CODEDEPLOY_APPLICATION} \\
                                                 --deployment-group-name ${CODEDEPLOY_DEPLOYMENT_GROUP} \\
                                                 --query 'deployments' \\
                                                 --status-filter 'InProgress,Queued' \\
                                                 --output text \\
                                                 --region ${AWS_REGION}
                                                 """).trim()

                    // AWS CLI의 text 출력은 줄 바꿈(\n)이나 탭(\t)으로 분리될 수 있습니다.
                    // Groovy에서 trim() 후 공백 기준으로 분리하여 배열로 만듭니다.
                    def activeDeployments = []
                    if (activeDeploymentIds) {
                        activeDeployments = activeDeploymentIds.split('\\s+').findAll { it != '' }
                    }

                    if (activeDeployments) {
                        echo "Found active deployment(s): ${activeDeployments.join(', ')}. Attempting to stop them."
                        // 각 활성 배포에 대해 중지 명령 실행
                        activeDeployments.each { deploymentId ->
                            echo "Stopping deployment ${deploymentId}..."
                            sh "aws deploy stop-deployment --deployment-id ${deploymentId} --region ${AWS_REGION}"
                        }
                        // CodeDeploy가 배포를 중지하는 데 시간이 걸릴 수 있으므로 잠시 대기
                        sleep 10
                        echo "Active deployments stopped or being stopped. Proceeding with new deployment."
                    } else {
                        echo "No active CodeDeploy deployments found. Proceeding with new deployment."
                    }
                    
                    // --- 수정된 코드 끝 ---
                    
                    // AWS CodeDeploy API를 호출하여 새 배포 시작
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
