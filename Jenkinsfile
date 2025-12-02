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
                    
                    // -plain.jar을 제외한 모든 JAR 파일을 찾습니다. (실행 가능한 Fat JAR만 대상)
                    def executableJarCandidates = sh(returnStdout: true, script: "find ${jarDirPath} -maxdepth 1 -name '*.jar' ! -name '*-plain.jar'").trim().split('\n').findAll { it.trim() != '' }
                    
                    def executableJar = ""
                    if (executableJarCandidates.size() == 1) {
                        executableJar = executableJarCandidates[0]
                        echo "Found single executable Fat JAR: ${executableJar}"
                    } else if (executableJarCandidates.size() > 1) {
                        error "Multiple executable Fat JAR files found. Cannot determine which one to use: ${executableJarCandidates.join(', ')}. Please refine your Gradle build or specify manually."
                    } else {
                        // 실행 가능한 JAR 파일이 하나도 없는 경우 오류
                        error "No executable Fat JAR file (non-plain.jar) found in ${jarDirPath}. Please check your Gradle build process. Ensure 'bootJar' is configured and producing a Fat JAR."
                    }

                    if (executableJar) {
                        sh "cp ${executableJar} recipe/app.jar"
                        echo "Copied executable JAR (${executableJar}) to recipe/app.jar for Docker build."
                        // 이곳에서 "file recipe/app.jar" 명령어를 제거했습니다.
                    } else {
                        error "Logical error: executableJar should have been set. Please re-check script logic."
                    }
                }
            }
        }
        
        stage('Docker Build & Push to ECR') {
    steps {
        script {
            echo "--- Logging into ECR ---"
            sh """
                aws ecr get-login-password --region ${AWS_REGION} \
                | docker login --username AWS --password-stdin ${ECR_REGISTRY}
            """

            echo "--- Building Docker Image ---"
            sh """
                docker build -t recipe-app:${BUILD_NUMBER} recipe
            """

            echo "--- Tagging Docker Image ---"
            sh """
                docker tag recipe-app:${BUILD_NUMBER} ${ECR_REGISTRY}:${BUILD_NUMBER}
            """

            echo "--- Pushing Docker Image to ECR ---"
            sh """
                docker push ${ECR_REGISTRY}:${BUILD_NUMBER}
            """

            echo "--- Docker Image Pushed: ${ECR_REGISTRY}:${BUILD_NUMBER} ---"
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
                    sh "cp recipe/app.jar ." // app.jar 다시 포함 (이번에는 올바른 JAR)
                    echo "DEBUG: All deployment scripts, ECR image value, and app.jar prepared for zipping."

                    echo "DEBUG: Zipping deployment artifacts (appspec.yml, scripts, app.jar, ECR_IMAGE_VALUE.txt)..."
                    sh "zip -r deployment.zip appspec.yml scripts app.jar ECR_IMAGE_VALUE.txt"
                    echo "DEBUG: deployment.zip created."

                    echo "DEBUG: Uploading deployment.zip to S3://${S3_BUCKET}/recipe-app/${env.BUILD_NUMBER}.zip"
                    sh "aws s3 cp deployment.zip s3://${S3_BUCKET}/recipe-app/${env.BUILD_NUMBER}.zip"
                    echo "DEBUG: deployment.zip uploaded to S3."

                    // --- Debugging appspec.yml content ---
                    echo "--- VERIFYING appspec.yml content for CodeDeploy ---"
                    sh "cat appspec.yml"
                    echo "--- END appspec.yml VERIFICATION ---"

                    // --- CodeDeploy 활성 배포 감지 및 중지 로직 (AWS CLI 파라미터 오류 우회) ---
                    def activeDeploymentsToStop = []
                    def checkableStatuses = ['Created', 'Queued', 'InProgress', 'Deploying']
                    
                    echo "Checking for active CodeDeploy deployments in group ${CODEDEPLOY_DEPLOYMENT_GROUP} using a robust method..."
                    
                    try {
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

                                    if (checkableStatuses.contains(currentStatus)) {
                                        activeDeploymentsToStop.add(deploymentId)
                                    }
                                } catch (e) {
                                    echo "WARNING: Failed to get status for deployment ${deploymentId}. It might be invalid or already cleaned up. Error: ${e.message}"
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
                            sh "aws deploy stop-deployment --deployment-id ${deploymentId} --region ${AWS_REGION} || true"
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
