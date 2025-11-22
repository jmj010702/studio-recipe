pipeline {
    agent any // Jenkins 워커 노드에서 실행
    //agent {
        // any -> node로 변경, retries 2는 Jenkins 재시작 등으로 빌드가 실패하면 최대 2번 시도
        //node{
            //label 'master'
            //retries 2
        //}
    //}

    environment {
        ECR_REPO_NAME = "recipe-app" // ECR Repository
        ECR_REPO_URL = "516175389011.dkr.ecr.ap-northeast-2.amazonaws.com/${ECR_REPO_NAME}"
        AWS_REGION = "ap-northeast-2"
        S3_ARTIFACT_BUCKET = "recipe-app-codedeploy-artifacts-516175389011"
        
        // CodeDeploy 애플리케이션 및 배포 그룹
        CODE_DEPLOY_APP_NAME = "recipe-app"
        CODE_DEPLOY_DEPLOYMENT_GROUP_NAME = "recipe-app-blue-green-deployment-group" //그룹명

        SECRETS_MANAGER_SECRET_ID = "recipe-app-secrets" // Secret ID 통일
        
        GITHUB_CREDENTIAL_ID = 'JG'
        AWS_CREDENTIAL_ID = 'AWS'
    }

    stages {
        stage('Checkout Source') {
            steps {
                echo '1. Checking out source code from GitHub...'
                git branch: 'main', credentialsId: GITHUB_CREDENTIAL_ID, url: 'https://github.com/stayonasDev/studio-recipe.git'
            }
        }

        stage('Build Application & Docker Image') {
            steps {
                script {
                    echo '2. Navigating to recipe directory...'
                    dir('recipe') { // !!! 'recipe' 폴더로 이동 !!!
                        echo '   Building Spring Boot application with Gradle...'
                        sh 'chmod +x gradlew'
                        sh './gradlew clean build --stacktrace'

                        echo '3. Building Docker image for backend application...'
                        
                        withAWS(credentials: AWS_CREDENTIAL_ID, region: AWS_REGION) {
                            sh "aws ecr get-login-password --region ${AWS_REGION} | docker login --username AWS --password-stdin ${ECR_REPO_URL}"
                            sh "docker build -t ${ECR_REPO_URL}:${env.BUILD_NUMBER} ."
                            sh "docker push ${ECR_REPO_URL}:${env.BUILD_NUMBER}"
                        }
                    }
                }
            }
        }

        stage('Fetch Secrets for Deployment') {
            steps {
                script {
                    echo '4. Fetching application secrets from AWS Secrets Manager...'
                    withAWS(credentials: AWS_CREDENTIAL_ID, region: AWS_REGION) {
                        def secretsJson = sh(returnStdout: true, script: "aws secretsmanager get-secret-value --secret-id ${SECRETS_MANAGER_SECRET_ID} --query SecretString --output text")
                        def secrets = readJSON text: secretsJson
                        
                        def envVarsString = ''
                        secrets.each { key, value ->
                            envVarsString += "-e ${key}=\"${value}\" "
                        }
                        env.ENV_VARS_FOR_DOCKER = envVarsString.trim()
                        echo "Secrets fetched and prepared for Docker: ${env.ENV_VARS_FOR_DOCKER.minus(~/DATABASE_PASSWORD="[^"]*"/).minus(~/MY_APP_SECRET="[^"]*"/)}"
                    }
                }
            }
        }

        stage('Deploy Application with CodeDeploy') {
            steps {
                script {
                    echo '5. Deploying backend application to EC2 via CodeDeploy (Blue/Green Mode)...'
                    withAWS(credentials: AWS_CREDENTIAL_ID, region: AWS_REGION) {
                        def s3Key = "recipe-app/${env.BUILD_NUMBER}.zip"
                        
                        // `recipe` 폴더만 압축 (appspec.yml과 scripts 폴더 포함)
                        sh "zip -r recipe-app.zip recipe -x 'recipe/.git*' -x 'recipe/.jenkins*'"
                        
                        sh "aws s3 cp recipe-app.zip s3://${S3_ARTIFACT_BUCKET}/${s3Key}"

                        sh """
                            aws deploy create-deployment \
                                --application-name ${CODE_DEPLOY_APP_NAME} \
                                --deployment-group-name ${CODE_DEPLOY_DEPLOYMENT_GROUP_NAME} \
                                --deployment-config-name CodeDeployDefault.OneAtATime \
                                --description "Blue/Green Deployment triggered by Jenkins build ${env.BUILD_NUMBER}" \
                                --s3-location bucket=${S3_ARTIFACT_BUCKET},key=${s3Key},bundleType=zip \
                                --region ${AWS_REGION}
                        """
                        echo "CodeDeploy Blue/Green deployment initiated."
                    }
                }
            }
        }
    }
    post {
        always {
            echo 'CI/CD Pipeline finished.'
        }
        success {
            echo 'Blue/Green Deployment successful! Check your application via ALB DNS.'
        }
        failure {
            echo 'Blue/Green Deployment failed. Check Jenkins logs and CodeDeploy console.'
        }
    }
}
