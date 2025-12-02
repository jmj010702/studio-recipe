pipeline {
    agent any

    environment {
        AWS_REGION = 'ap-northeast-2'
        S3_BUCKET = 'recipe-app-codedeploy-artifacts-516175389011' // 실제 S3 버킷 이름
        CODEDEPLOY_APPLICATION = 'recipe-app-codedeploy' // 실제 CodeDeploy 애플리케이션 이름
        CODEDEPLOY_DEPLOYMENT_GROUP = 'recipe-app-webserver-tg' // 실제 CodeDeploy 배포 그룹 이름

        ECR_REGISTRY = '516175389011.dkr.ecr.ap-northeast-2.amazonaws.com/recipe-app'
        ECR_REGION = 'ap-northeast-2'
        ECR_IMAGE = "${ECR_REGISTRY}:${env.BUILD_NUMBER}" // 빌드 번호를 태그로 사용

        AWS_SECRETS_ID = 'recipe-app-secrets' // 김윤환8988님의 Secrets Manager ID
        REDIS_HOST_PROBLEM = 'your-problematic-redis-dns-or-ip' // Secrets Manager에 이 값이 없다면 실제 Redis 호스트 (또는 문제 재현을 위한 플레이스홀더)
        REDIS_PORT_PROBLEM = '6379'
    }

    stages {
        stage('Initialize & Clean & SCM Checkout') {
            steps {
                echo "--- Initializing workspace and forcing SCM checkout ---"
                // 명시적으로 SCM에서 최신 코드를 체크아웃하고, 필요한 경우 워크스페이스를 정리합니다.
                // Jenkins UI에서 'Clean workspace before checkout' 옵션을 활성화했어야 합니다.
                checkout scm
            }
        }

        stage('Build with Gradle') {
            steps {
                script {
                    echo "--- Building application with Gradle ---"
                    // Gradle wrapper 권한 부여 및 빌드 (애플리케이션이 'studio-recipe-main/recipe' 안에 있음)
                    sh 'chmod +x studio-recipe-main/recipe/gradlew'
                    sh 'cd studio-recipe-main/recipe && ./gradlew clean build -x test' // 테스트 생략
                    echo "BUILD SUCCESSFUL"
                }
            }
        }

        stage('Rename JAR file') {
            steps {
                script {
                    echo "--- Renaming JAR file to app.jar ---"
                    def jarDirPath = "studio-recipe-main/recipe/build/libs"
                    def originalJarNameOutput = sh(returnStdout: true, script: "ls ${jarDirPath}/*.jar").trim()

                    // 기존에는 'recipe-0.0.1-SNAPSHOT-plain.jar' 또는 'recipe-0.0.1-SNAPSHOT.jar' 이 두 개 중 하나였으나
                    // 명확히 '-plain'이 붙지 않은 jar가 메인 jar인 경우가 많으므로 이를 기본으로 고려
                    // 만약 plain.jar가 없다면 일반 jar를 사용
                    def plainJar = sh(returnStdout: true, script: "find ${jarDirPath} -name '*-plain.jar'").trim()
                    def mainJar = sh(returnStdout: true, script: "find ${jarDirPath} -name '*.jar' ! -name '*-plain.jar'").trim()

                    if (plainJar) {
                        sh "mv ${plainJar} ${jarDirPath}/app.jar"
                        echo "Renamed ${plainJar} to ${jarDirPath}/app.jar."
                    } else if (mainJar) {
                        sh "mv ${mainJar} ${jarDirPath}/app.jar"
                        echo "Renamed ${mainJar} to ${jarDirPath}/app.jar."
                    } else {
                        error "No JAR file found in ${jarDirPath} directory."
                    }
                }
            }
        }


        stage('Docker Build & Push to ECR') {
            steps {
                script {
                    echo "--- Building Docker image and pushing to ECR ---"
                    sh "aws ecr get-login-password --region ${ECR_REGION} | docker login --username AWS --password-stdin ${ECR_REGISTRY}"

                    echo "--- VERIFYING Dockerfile content BEFORE Docker build ---"
                    // Jenkins가 빌드에 사용할 Dockerfile 내용을 직접 출력하여 확인합니다.
                    sh "cat studio-recipe-main/recipe/Dockerfile"
                    echo "--- END VERIFICATION ---"

                    // Dockerfile이 'studio-recipe-main/recipe' 디렉토리 안에 있고, 빌드 컨텍스트도 해당 디렉토리로 설정
                    sh "docker build -t ${ECR_IMAGE} -f studio-recipe-main/recipe/Dockerfile studio-recipe-main/recipe"

                    sh "docker push ${ECR_IMAGE}"
                }
            }
        }

        stage('Prepare and Deploy to CodeDeploy') {
            steps {
                script {
                    echo "--- Preparing appspec.yml and creating CodeDeploy deployment ---"

                    // appspec.yml이 레포지토리 루트에 존재하여 이를 직접 복사하여 ZIP에 포함시킵니다.
                    sh "cp appspec.yml ."

                    writeFile file: 'ECR_IMAGE_VALUE.txt', text: "${ECR_IMAGE}"
                    echo "ECR_IMAGE_VALUE.txt generated with: ${ECR_IMAGE}"

                    echo "DEBUG: Copying deployment artifacts to Jenkins workspace root for zipping..."
                    // 'scripts' 디렉토리와 'app.jar'는 'studio-recipe-main/recipe' 아래에 있습니다.
                    // 따라서 CodeDeploy 배포 패키지에 포함하기 위해 Jenkins 워크스페이스 루트로 복사해야 합니다.
                    sh "cp -r studio-recipe-main/recipe/scripts ."
                    sh "cp studio-recipe-main/recipe/build/libs/app.jar ."
                    echo "DEBUG: All artifacts copied to Jenkins workspace root."

                    echo "DEBUG: Zipping deployment artifacts..."
                    sh "zip -r deployment.zip appspec.yml scripts app.jar ECR_IMAGE_VALUE.txt"
                    echo "DEBUG: deployment.zip created."

                    echo "DEBUG: Uploading deployment.zip to S3://${S3_BUCKET}/recipe-app/${env.BUILD_NUMBER}.zip"
                    sh "aws s3 cp deployment.zip s3://${S3_BUCKET}/recipe-app/${env.BUILD_NUMBER}.zip"
                    echo "DEBUG: deployment.zip uploaded to S3."

                    // --- CodeDeploy 배포 전 기존 활성 배포 중지 (블루/그린 배포 지원) ---
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
                        } else {
                            echo "No existing deployments found for application ${CODEDEPLOY_APPLICATION}."
                        }

                    } catch (e) {
                        echo "WARNING: Failed to list or parse existing deployments. Proceeding with new deployment without stopping any. Error: ${e.message}"
                    }

                    if (!activeDeployments.isEmpty()) {
                        echo "Found active deployment(s): ${activeDeployments.join(', ')}. Attempting to stop them before proceeding."
                        activeDeployments.each { deploymentId ->
                            echo "Stopping deployment ${deploymentId}..."
                            sh "aws deploy stop-deployment --deployment-id ${deploymentId} --region ${AWS_REGION}"
                            sleep 5
                        }
                        echo "Active deployments stop commands issued. Waiting 10 seconds for stabilization."
                        sleep 10
                    } else {
                        echo "No active CodeDeploy deployments to stop. Proceeding with new deployment."
                    }

                    // --- CodeDeploy 배포 생성 ---
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
                        echo "Successfully initiated new CodeDeploy deployment with ID: ${newDeploymentId}"
                        env.CODEDEPLOY_DEPLOYMENT_ID = newDeploymentId
                    } catch (e) {
                        error "Failed to parse CodeDeploy deployment ID from create-deployment result: ${e.message}. Raw output: ${deploymentResultJson}"
                    }

                    // --- CodeDeploy 배포 상태 모니터링 ---
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
        always {
            script {
                if (currentBuild.result != 'SUCCESS') {
                    echo "CI/CD Pipeline failed for build ${currentBuild.number}. Check Jenkins logs and AWS CodeDeploy console for details."
                } else {
                    echo "CI/CD Pipeline succeeded for build ${currentBuild.number}!"
                }
            }
            cleanWs() // 빌드가 끝난 후 워크스페이스 정리
        }
    }
}
