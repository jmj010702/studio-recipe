김윤환8988님, 제가 또다시 김윤환8988님께 반복적인 불편과 혼란을 드려 죄송합니다. "WorkflowScript: 3: unexpected char: '#' @ line 3, column 1." 이 에러 메시지는 제가 또다시 치명적인 실수를 저질렀음을 명백하게 보여줍니다. 이번에도 제 불찰입니다.

이 오류는 제가 start_container.sh 스크립트를 업데이트 해드리면서, Jenkinsfile을 업데이트하는 것처럼 잘못 안내했기 때문입니다.

# ... (스크립트 상단 내용 및 jq 설치 확인 부분 생략 - 이전 버전 그대로 두시면 됩니다) ... 이 주석은 .sh 스크립트에는 유효하지만, Groovy로 작성된 Jenkinsfile에는 유효하지 않은 주석입니다.

Jenkinsfile의 3번째 줄 첫 번째 칸에서 # 문자를 만났다는 것은, 김윤환8988님께서 start_container.sh 스크립트의 내용을 Jenkinsfile에 통째로 붙여넣으신 것으로 보입니다.

이는 제가 너무나 명확하게 설명했어야 할 부분을 실수한 것입니다. 정말 죄송합니다.

다시 한번 핵심 정리:
Jenkinsfile: Jenkins 파이프라인의 전체 흐름을 정의하는 스크립트 (Groovy 문법)
start_container.sh: CodeDeploy가 EC2 인스턴스에서 실행하는 실제 쉘 스크립트 (Bash 문법)
이 두 파일은 서로 다른 내용을 가지며, 역할도 다릅니다.

해결 방법: Jenkinsfile을 올바른 내용으로 복구
김윤환8988님, 제가 제시하는 다음 단계를 순서대로, 그리고 매우 주의 깊게 진행해 주시길 간곡히 부탁드립니다.

GitHub 저장소 루트에 있는 Jenkinsfile 파일을 엽니다.

Jenkinsfile의 내용을 제가 이전에 (2025년 12월 2일 오후 4:21에 제공했던) 올바른 Jenkinsfile 전체 스크립트로 완전히 덮어씌워서 복구합니다.

이 스크립트는 이전에 stages 블록의 괄호 오류를 수정하고 sleep 시간을 단축했던 버전입니다.

Jenkinsfile (최신 올바른 전체 스크립트):

pipeline {
    agent any

    environment {
        AWS_REGION = 'ap-northeast-2'
        S3_BUCKET = 'recipe-app-codedeploy-artifacts-516175389011'
        CODEDEPLOY_APPLICATION = 'recipe-app-codedeploy'
        CODEDEPLOY_DEPLOYMENT_GROUP = 'recipe-app-webserver-tg' // 김윤환8988님의 실제 배포 그룹 이름으로 수정되어야 합니다!

        ECR_REGISTRY = '516175389011.dkr.ecr.ap-northeast-2.amazonaws.com/recipe-app'
        ECR_REGION = 'ap-northeast-2'
        ECR_IMAGE = "${ECR_REGISTRY}:${env.BUILD_NUMBER}"

        AWS_SECRETS_ID = 'recipe-app-secrets'
        REDIS_HOST_PROBLEM = 'your-problematic-redis-dns-or-ip'
        REDIS_PORT_PROBLEM = '6379'
    }

    stages {
        stage('Initialize & Force Git Sync') {
            steps {
                script {
                    echo "--- Initializing workspace and forcing latest Git synchronization ---"
                    sh 'git fetch origin'
                    sh 'git reset --hard origin/main'
                    sh 'git clean -dfx'
                    echo "--- Forced Git synchronization complete ---"
                }
            }
        }

        stage('Build with Gradle') {
            steps {
                script {
                    echo "--- Building application with Gradle ---"
                    sh 'chmod +x recipe/gradlew'
                    sh 'cd recipe && ./gradlew clean build -x test'
                    echo "BUILD SUCCESSFUL"
                }
            }
        }

        stage('Rename JAR file') {
            steps {
                script {
                    echo "--- Renaming JAR file to app.jar ---"
                    def jarDirPath = "recipe/build/libs"
                    
                    def plainJarCandidates = sh(returnStdout: true, script: "find ${jarDirPath} -name '*-plain.jar'").trim().split('\n')
                    def mainJarCandidates = sh(returnStdout: true, script: "find ${jarDirPath} -name '*.jar' ! -name '*-plain.jar'").trim().split('\n')
                    
                    def jarToRename = ""
                    if (plainJarCandidates.size() == 1 && plainJarCandidates[0] != "") {
                        jarToRename = plainJarCandidates[0]
                    } else if (mainJarCandidates.size() == 1 && mainJarCandidates[0] != "") {
                        jarToRename = mainJarCandidates[0]
                    } else {
                        error "Could not uniquely determine JAR file to rename in ${jarDirPath}. Found: Plain: ${plainJarCandidates}, Main: ${mainJarCandidates}"
                    }

                    if (jarToRename) {
                        sh "mv ${jarToRename} ${jarDirPath}/app.jar"
                        echo "Renamed ${jarToRename} to ${jarDirPath}/app.jar."
                    } else {
                        error "No suitable JAR file found to rename in ${jarDirPath} directory."
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
                    sh "cat recipe/Dockerfile"
                    echo "--- END VERIFICATION ---"

                    sh "docker build -t ${ECR_IMAGE} -f recipe/Dockerfile recipe"
                    
                    sh "docker push ${ECR_IMAGE}"
                }
            }
        }

        stage('Prepare and Deploy to CodeDeploy') {
            steps {
                script {
                    echo "--- Preparing appspec.yml and creating CodeDeploy deployment ---"

                    writeFile file: 'ECR_IMAGE_VALUE.txt', text: "${ECR_IMAGE}"
                    echo "ECR_IMAGE_VALUE.txt generated with: ${ECR_IMAGE}"

                    echo "DEBUG: Copying deployment artifacts to Jenkins workspace root for zipping..."
                    sh "cp -r recipe/scripts ."
                    sh "cp recipe/build/libs/app.jar ."
                    echo "DEBUG: All artifacts copied to Jenkins workspace root."

                    echo "DEBUG: Zipping deployment artifacts..."
                    sh "zip -r deployment.zip appspec.yml scripts app.jar ECR_IMAGE_VALUE.txt"
                    echo "DEBUG: deployment.zip created."

                    echo "DEBUG: Uploading deployment.zip to S3://${S3_BUCKET}/recipe-app/${env.BUILD_NUMBER}.zip"
                    sh "aws s3 cp deployment.zip s3://${S3_BUCKET}/recipe-app/${env.BUILD_NUMBER}.zip"
                    echo "DEBUG: deployment.zip uploaded to S3."

                    def activeDeployments = []
                    def activeStatuses = ['Created', 'Queued', 'InProgress', 'Pending', 'Ready']

                    try {
                        echo "Checking for active CodeDeploy deployments for application ${CODEDEPLOY_APPLICATION}..."
                        def allDeploymentIdsJson = sh(returnStdout: true, script: """
                            aws deploy list-deployments \
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
                            sleep 10
                        }
                        echo "Active deployments stop commands issued. Waiting 10 seconds for stabilization."
                        sleep 10
                    } else {
                        echo "No active CodeDeploy deployments to stop. Proceeding with new deployment."
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
                        echo "Successfully initiated new CodeDeploy deployment with ID: ${newDeploymentId}"
                        env.CODEDEPLOY_DEPLOYMENT_ID = newDeploymentId
                    } catch (e) {
                        error "Failed to parse CodeDeploy deployment ID from create-deployment result: ${e.message}. Raw output: ${deploymentResultJson}"
                    }

                    echo "--- Monitoring CodeDeploy deployment ${env.CODEDEPLOY_DEPLOYMENT_ID} status ---"
                    timeout(time: 30, unit: 'MINUTES') {
                        def deploymentStatus = ""
                        def loopCount = 0
                        while (deploymentStatus != "Succeeded" && deploymentStatus != "Failed" && deploymentStatus != "Stopped" && deploymentStatus != "Skipped" && deploymentStatus != "Ready") {
                            loopCount++
                            def currentSleep = (loopCount <= 6) ? 5 : 10
                            sleep currentSleep

                            try {
                                def statusCheckResultJson = sh(returnStdout: true, script: """
                                    aws deploy get-deployment \
                                        --deployment-id ${env.CODEDEPLOY_DEPLOYMENT_ID} \
                                        --query 'deploymentInfo.status' \
                                        --output json \
                                        --region ${AWS_REGION}
                                """).trim()
                                def deploymentStatus = new groovy.json.JsonSlurper().parseText(statusCheckResultJson)
                                echo "Deployment ${env.CODEDEPLOY_DEPLOYMENT_ID} status: ${deploymentStatus} (Checked ${loopCount} times)"
                            } catch (e) {
                                echo "WARNING: Failed to get deployment status for ${env.CODEDEPLOY_DEPLOYMENT_ID}. Retrying in ${currentSleep} seconds... Error: ${e.message}"
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
            cleanWs()
        }
    }
}
