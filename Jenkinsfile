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

        stage('Rename JAR file and Prepare for Docker') {
            steps {
                script {
                    echo "--- Preparing JAR file for Docker build ---"
                    def jarDirPath = "recipe/build/libs"
                    
                    def plainJarCandidates = sh(returnStdout: true, script: "find ${jarDirPath} -name '*-plain.jar'").trim().split('\n')
                    def mainJarCandidates = sh(returnStdout: true, script: "find ${jarDirPath} -name '*.jar' ! -name '*-plain.jar'").trim().split('\n')
                    
                    def jarToUse = ""
                    if (plainJarCandidates.size() == 1 && plainJarCandidates[0] != "") {
                        jarToUse = plainJarCandidates[0]
                    } else if (mainJarCandidates.size() == 1 && mainJarCandidates[0] != "") {
                        jarToUse = mainJarCandidates[0]
                    } else {
                        error "Could not uniquely determine JAR file in ${jarDirPath}. Found: Plain: ${plainJarCandidates}, Main: ${mainJarCandidates}"
                    }

                    if (jarToUse) {
                        sh "cp ${jarToUse} recipe/app.jar"
                        echo "Copied ${jarToUse} to recipe/app.jar for Docker build."
                    } else {
                        error "No suitable JAR file found to copy for Docker build."
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
                    sh "cp recipe/app.jar ." // app.jar 다시 포함
                    echo "DEBUG: All deployment scripts, ECR image value, and app.jar prepared for zipping."

                    echo "DEBUG: Zipping deployment artifacts (appspec.yml, scripts, app.jar, ECR_IMAGE_VALUE.txt)..."
                    sh "zip -r deployment.zip appspec.yml scripts app.jar ECR_IMAGE_VALUE.txt" // app.jar 다시 포함
                    echo "DEBUG: deployment.zip created."

                    echo "DEBUG: Uploading deployment.zip to S3://${S3_BUCKET}/recipe-app/${env.BUILD_NUMBER}.zip"
                    sh "aws s3 cp deployment.zip s3://${S3_BUCKET}/recipe-app/${env.BUILD_NUMBER}.zip"
                    echo "DEBUG: deployment.zip uploaded to S3."

                    // --- Debugging appspec.yml content ---
                    echo "--- VERIFYING appspec.yml content for CodeDeploy ---"
                    sh "cat appspec.yml" // Jenkins workspace root에 있는 appspec.yml 출력
                    echo "--- END appspec.yml VERIFICATION ---"

                    // --- CodeDeploy 활성 배포 감지 및 중지 로직 (AWS CLI 파라미터 오류 우회) ---
                    def activeDeploymentsToStop = []
                    // CodeDeploy API의 유효한 활성 상태들 (대문자, 띄어쓰기 없음) - API 문서에 기반하여 다시 정확히 명시
                    def activeApiStatuses = ['Created', 'Queued', 'InProgress', 'Ready', 'Deploying', 'Pending'] // API 문서 참고하여 유효한 값으로 조정
                    
                    echo "Checking for active CodeDeploy deployments in group ${CODEDEPLOY_DEPLOYMENT_GROUP} using a robust method..."
                    
                    try {
                        // 모든 배포를 가져온 다음, Groovy 내부에서 상태 필터링
                        def allDeploymentsJson = sh(returnStdout: true, script: '''
                            aws deploy list-deployments \
                                --application-name ''' + CODEDEPLOY_APPLICATION + ''' \
                                --deployment-group-name ''' + CODEDEPLOY_DEPLOYMENT_GROUP + ''' \
                                --query "deployments" \
                                --output json \
                                --region ''' + AWS_REGION + '''
                        ''').trim()

                        echo "DEBUG: Raw output from list-deployments (all statuses) for group ${CODEDEPLOY_DEPLOYMENT_GROUP}: ${allDeploymentsJson}"
                        
                        def allDeploymentIds = new groovy.json.JsonSlurper().parseText(allDeploymentsJson)
                        
                        if (allDeploymentIds && !allDeploymentIds.isEmpty()) {
                            allDeploymentIds.each { deploymentId ->
                                try {
                                    def deploymentInfoJson = sh(returnStdout: true, script: '''
                                        aws deploy get-deployment \
                                            --deployment-id ''' + deploymentId + ''' \
                                            --query "deploymentInfo.status" \
                                            --output json \
                                            --region ''' + AWS_REGION + '''
                                    ''').trim()
                                    def currentStatus = new groovy.json.JsonSlurper().parseText(deploymentInfoJson)
                                    echo "DEBUG: Deployment ${deploymentId} has status: ${currentStatus}"

                                    // API가 반환하는 상태값은 대문자로 반환되는 경향이 있으므로, 비교 시에도 일관성 유지
                                    if (activeApiStatuses.contains(currentStatus) && currentStatus != 'Succeeded' && currentStatus != 'Failed' && currentStatus != 'Stopped') {
                                        activeDeploymentsToStop.add(deploymentId)
                                    }
                                } catch (e) {
                                    echo "WARNING: Failed to get status for deployment ${deploymentId}. It might have already completed/failed. Error: ${e.message}"
                                }
                            }
                            echo "DEBUG: Identified active deployments to stop after filtering: ${activeDeploymentsToStop}"
                        } else {
                            echo "No deployments found at all for application ${CODEDEPLOY_APPLICATION} in group ${CODEDEPLOY_DEPLOYMENT_GROUP}."
                        }

                    } catch (e) {
                        echo "ERROR: Failed to list or process deployments for group ${CODEDEPLOY_DEPLOYMENT_GROUP}. Error: ${e.message}"
                        error "CodeDeploy deployment listing failed. Please ensure IAM permissions are correct and the deployment group exists."
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
                        echo "No currently active CodeDeploy deployments to stop in group ${CODEDEPLOY_DEPLOYMENT_GROUP}. Proceeding with new deployment."
                    }

                    echo "--- Initiating new CodeDeploy deployment ---"
                    def createDeploymentCommand = '''
                        aws deploy create-deployment \
                            --application-name ''' + CODEDEPLOY_APPLICATION + ''' \
                            --deployment-group-name ''' + CODEDEPLOY_DEPLOYMENT_GROUP + ''' \
                            --deployment-config-name CodeDeployDefault.OneAtATime \
                            --description "Blue/Green Deployment triggered by Jenkins build ''' + env.BUILD_NUMBER + '''" \
                            --s3-location bucket=''' + S3_BUCKET + ''',key=recipe-app/''' + env.BUILD_NUMBER + '''.zip,bundleType=zip \
                            --region ''' + AWS_REGION + '''
                    '''
                    echo "DEBUG: Running create-deployment command: ${createDeploymentCommand}"
                    def deploymentResultJson = sh(returnStdout: true, script: createDeploymentCommand).trim()

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
                                def getDeploymentStatusCommand = '''
                                    aws deploy get-deployment \
                                        --deployment-id ''' + env.CODEDEPLOY_DEPLOYMENT_ID + ''' \
                                        --query "deploymentInfo.status" \
                                        --output json \
                                        --region ''' + AWS_REGION + '''
                                '''
                                def statusCheckResultJson = sh(returnStdout: true, script: getDeploymentStatusCommand).trim()
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
