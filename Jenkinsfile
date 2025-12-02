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

        
