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
                    // ... (파일 찾기 로직 생략, jarToRename 변수 생성까지는 동일)
            
                    if (jarToRename) {
                        // mv 대신 cp를 사용하여 Jenkins 워크스페이스 루트로 복사합니다.
                        sh "cp ${jarToRename} app.jar" 
                        echo "Copied ${jarToRename} to app.jar in Jenkins root."
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
                    
                    sh "cp app.jar recipe/"
                    echo "Copied app.jar from root to recipe/ for Docker build context."

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
                    // sh "cp recipe/build/libs/app.jar ."
                    sh "cp recipe/app.jar ."
                    echo "DEBUG: All artifacts copied to Jenkins workspace root."

                    echo "DEBUG: Zipping deployment artifacts..."
                    sh "zip -r deployment.zip appspec.yml scripts app.jar ECR_IMAGE_VALUE.txt"
                    echo "DEBUG: deployment.zip created."

                    echo "DEBUG: Uploading deployment.zip to S3://${S3_BUCKET}/recipe-app/${env.BUILD_NUMBER}.zip"
                    sh "aws s3 cp deployment.zip s3://${S3_BUCKET}/recipe-app/${env.BUILD_NUMBER}.zip"
                    echo "DEBUG: deployment.zip uploaded to S3."

                    // --- CodeDeploy 활성 배포 감지 및 중지 로직 강화 (MISSING ARG FIX 및 문법 수정) ---
                    def activeDeploymentsToStop = []
                    def checkStatuses = ['Created', 'Queued', 'InProgress', 'Pending', 'Ready']

                    echo "Checking for active CodeDeploy deployments in group ${CODEDEPLOY_DEPLOYMENT_GROUP}..."
                    
                    try {
                        def deploymentsJson = sh(returnStdout: true, script: """
                            aws deploy list-deployments \\
                                --application-name ${CODEDEPLOY_APPLICATION} \\
                                --deployment-group-name ${CODEDEPLOY_DEPLOYMENT_GROUP} \\
                                --include-only-statuses ${checkStatuses.join(',')} \\
                                --query 'deployments' \\
                                --output json \\
                                --region ${AWS_REGION}
                        """).trim()

                        def deploymentIds = new groovy.json.JsonSlurper().parseText(deploymentsJson)
                        
                        if (deploymentIds && !deploymentIds.isEmpty()) {
                            activeDeploymentsToStop.addAll(deploymentIds) // 필터링이 이미 API에서 되었으므로 그대로 추가
                        } else {
                            echo "No active deployments found for application ${CODEDEPLOY_APPLICATION} in group ${CODEDEPLOY_DEPLOYMENT_GROUP}."
                        }

                    } catch (e) {
                        echo "WARNING: Failed to list active deployments for group ${CODEDEPLOY_DEPLOYMENT_GROUP}. Error: ${e.message}"
                        echo "This might lead to 'DeploymentLimitExceededException'. Please ensure IAM permissions are correct and the deployment group exists."
                    }

                    if (!activeDeploymentsToStop.isEmpty()) {
                        echo "Found active deployment(s) in group ${CODEDEPLOY_DEPLOYMENT_GROUP}: ${activeDeploymentsToStop.join(', ')}. Attempting to stop them."
                        activeDeploymentsToStop.each { deploymentId ->
                            echo "Stopping deployment ${deploymentId}..."
                            sh "aws deploy stop-deployment --deployment-id ${deploymentId} --region ${AWS_REGION}"
                            sleep 5
                        }
                        echo "Active deployments stop commands issued. Waiting 30 seconds for stabilization before new deployment."
                        sleep 30
                    } else {
                        echo "No active CodeDeploy deployments to stop in group ${CODEDEPLOY_DEPLOYMENT_GROUP}. Proceeding with new deployment."
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
                            def currentSleep = (loopCount <= 6) ? 5 : 10 // 처음 6번은 5초 간격, 이후 10초 간격
                            sleep currentSleep

                            try {
                                def statusCheckResultJson = sh(returnStdout: true, script: """
                                    aws deploy get-deployment \\
                                        --deployment-id ${env.CODEDEPLOY_DEPLOYMENT_ID} \\
                                        --query 'deploymentInfo.status' \\
                                        --output json \\
                                        --region ${AWS_REGION}
                                """).trim()
                                deploymentStatus = new groovy.json.JsonSlurper().parseText(statusCheckResultJson) 
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
