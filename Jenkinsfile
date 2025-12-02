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

                    // appspec.yml, scripts, app.jar 파일을 Jenkins 워크스페이스 루트로 복사
                    sh "cp appspec.yml ."
                    sh "cp -r ${BACKEND_DIR}/scripts ."
                    sh "cp ${BACKEND_DIR}/build/libs/app.jar ." 

                    writeFile file: 'ECR_IMAGE_VALUE.txt', text: "${ECR_IMAGE}"
                    
                    echo "DEBUG: Zipping deployment artifacts..."
                    sh "zip -r deployment.zip appspec.yml scripts app.jar ECR_IMAGE_VALUE.txt"

                    echo "DEBUG: Uploading deployment.zip to S3://${S3_BUCKET}/recipe-app/${env.BUILD_NUMBER}.zip"
                    sh "aws s3 cp deployment.zip s3://${S3_BUCKET}/recipe-app/${env.BUILD_NUMBER}.zip"
                    
                    def activeDeployments = []
                    def activeStatuses = ['Created', 'Queued', 'InProgress', 'Pending', 'Ready']

                    try {
                        echo "Checking for active CodeDeploy deployments for application ${CODEDEPLOY_APPLICATION}..."
                        def allDeploymentIdsJson = sh(returnStdout: true, script: """
                            aws deploy list-deployments-by-application \
                                --application-name ${CODEDEPLOY_APPLICATION} \
                                --query 'deployments' \
                                --output json \
                                --region ${AWS_REGION}
                            """).trim()

                        def deploymentIdList = new groovy.json.JsonSlurper().parseText(allDeploymentIdsJson)

                        if (deploymentIdList && !deploymentIdList.isEmpty()) {
                            deploymentIdList.each { deploymentId ->
                                try {
                                    def deploymentStatusJson = sh(returnStdout: true, script: """
                                        aws deploy get-deployment \
                                            --deployment-id ${deploymentId} \
                                            --query 'deploymentInfo.status' \
                                            --output json \
                                            --region ${AWS_REGION}
                                        """).trim()
                                    def deploymentStatus = new groovy.json.JsonSlurper().parseText(deploymentStatusJson)

                                    if (activeStatuses.contains(deploymentStatus)) {
                                        activeDeployments.add(deploymentId)
                                    }
                                } catch (e) {
                                    echo "WARNING: Could not get status for deployment ID ${deploymentId}. It might be too old or invalid. Error: ${e.message}"
                                }
                            }
                        }
                    } catch (e) {
                        echo "WARNING: Failed to list or parse existing deployments. Proceeding with new deployment without stopping any. Error: ${e.message}"
                    }

                    if (!activeDeployments.isEmpty()) {
                        echo "Found active deployment(s): ${activeDeployments.join(', ')}. Attempting to stop them before proceeding."
                        activeDeployments.each { deploymentId ->
                            sh "aws deploy stop-deployment --deployment-id ${deploymentId} --region ${AWS_REGION}"
                            sleep 5
                        }
                        sleep 10
                    }

                    echo "--- Initiating new CodeDeploy deployment ---"
                    def deploymentResultJson = sh(returnStdout: true, script: """
                    aws deploy create-deployment \\
                        --application-name ${CODEDEPLOY_APPLICATION} \\
                        --deployment-group-name ${CODEDEPLOY_DEPLOYMENT_GROUP} \\
                        --deployment-config-name CodeDeployDefault.OneAtATime \\
                        --description "Blue/Green Deployment triggered by Jenkins build ${env.BUILD_NUMBER}" \\
                        --s3-location bucket=${S3_BUCKET},key=recipe-app/${env.BUILD_NUMBER}.zip,bundleType=zip \\
                        --region ${AWS_REGION}
                    """).trim()

                    def newDeploymentId = ""
                    try {
                        newDeploymentId = new groovy.json.JsonSlurper().parseText(deploymentResultJson).deploymentId
                        env.CODEDEPLOY_DEPLOYMENT_ID = newDeploymentId
                    } catch (e) {
                        error "Failed to parse CodeDeploy deployment ID from create-deployment result: ${e.message}. Raw output: ${deploymentResultJson}"
                    }

                    echo "--- Monitoring CodeDeploy deployment ${env.CODEDEPLOY_DEPLOYMENT_ID} status ---"
                    timeout(time: 30, unit: 'MINUTES') {
                        def deploymentStatus = ""
                        while (deploymentStatus != "Succeeded" && deploymentStatus != "Failed" && deploymentStatus != "Stopped" && deploymentStatus != "Skipped" && deploymentStatus != "Ready") {
                            sleep 30
                            try {
                                def statusCheckResultJson = sh(returnStdout: true, script: """
                                    aws deploy get-deployment \
                                        --deployment-id ${env.CODEDEPLOY_DEPLOYMENT_ID} \
                                        --query 'deploymentInfo.status' \
                                        --output json \
                                        --region ${AWS_REGION}
                                    """).trim()
                                deploymentStatus = new groovy.json.JsonSlurper().parseText(statusCheckResultJson)
                                echo "Deployment ${env.CODEDEPLOY_DEPLOYMENT_ID} status: ${deploymentStatus}"
                            } catch (e) {
                                echo "WARNING: Failed to get deployment status for ${env.CODEDEPLOY_DEPLOYMENT_ID}. Retrying... Error: ${e.message}"
                            }
                        }

                        if (deploymentStatus == "Failed" || deploymentStatus == "Stopped" || deploymentStatus == "Skipped") {
                            error "CodeDeploy deployment ${env.CODEDEPLOY_DEPLOYMENT_ID} failed or was stopped. Current status: ${deploymentStatus}"
                        } else {
                            echo "CodeDeploy deployment ${env.CODEDEPLOY_DEPLOYMENT_ID} succeeded!"
                        }
                    }
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
