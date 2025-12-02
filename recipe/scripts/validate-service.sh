// Jenkins Declarative Pipeline - Docker DooD (Docker-outside-of-Docker) 환경용

// 1. Global Environment (ECR, Image Name, AWS Region 설정)
environment {
    // [필수] 사용자의 실제 AWS 계정 ID와 ECR 리전을 기반으로 수정하세요.
    ECR_REGISTRY = '516175389011.dkr.ecr.ap-northeast-2.amazonaws.com' 
    IMAGE_NAME = 'recipe-app'
    AWS_REGION = 'ap-northeast-2' // ECR 및 CodeDeploy 리전 명시
    
    // 백엔드 프로젝트 루트 디렉토리 설정 (Git 루트 아래 'recipe' 폴더를 가리킴)
    BACKEND_DIR = 'recipe'
    
    // [배포 설정] S3 버킷 이름은 실제 CodeDeploy 아티팩트 버킷 이름으로 수정해야 합니다.
    S3_BUCKET = 'recipe-app-deploy-bucket' 
    CODE_DEPLOY_APP = 'recipe-app-codedeploy'
    CODE_DEPLOY_GROUP = 'recipe-app-deployment-group'
}

pipeline {
    agent any
    
    stages {
        stage('Checkout Code') {
            steps {
                echo "Checking out source code from SCM..."
                checkout scm
            }
        }

        stage('Build Spring Boot App (Gradle)') {
            steps {
                echo "--- Spring Boot 애플리케이션 빌드 ---"
                
                // 환경 변수 null 문제를 우회하기 위해 script 내부에서 경로를 명시적으로 'recipe'로 설정합니다.
                script {
                    def BACKEND_PATH = 'recipe' 
                    
                    echo "Guaranteed Backend Path: ${BACKEND_PATH}"
                    
                    // Gradle 실행 권한 부여
                    sh "chmod +x ${BACKEND_PATH}/gradlew"
                    // Gradle 빌드 수행
                    sh "${BACKEND_PATH}/gradlew clean build -p ${BACKEND_PATH}"
                }

                echo "--- JAR 파일 app.jar로 이름 변경 ---"
                script {
                    def BACKEND_PATH = 'recipe' 
                    
                    def jarFiles = findFiles(glob: "${BACKEND_PATH}/build/libs/*.jar")
                    if (jarFiles.length == 0) {
                        error "빌드 후 JAR 파일이 발견되지 않았습니다. 빌드 실패!"
                    }
                    def originalJarPath = jarFiles[0].path
                    def targetJarPath = "${BACKEND_PATH}/build/libs/app.jar"
                    
                    if (originalJarPath != targetJarPath) {
                        sh "mv ${originalJarPath} ${targetJarPath}"
                        echo "JAR 파일 이름을 ${targetJarPath}로 변경했습니다."
                    } else {
                        echo "JAR 파일 이름이 이미 ${targetJarPath}이므로 변경을 건너뛰닙니다."
                    }
                }
            }
        }
        
        stage('Build Docker Image and Push to ECR') {
            steps {
                echo "--- Docker ECR 로그인 및 이미지 빌드/푸시 ---"
                script {
                    // 환경 변수 null 문제를 우회하기 위해 명시적으로 설정
                    def BACKEND_PATH = 'recipe' 
                    def ECR_REGION = 'ap-northeast-2' 
                    def ECR_REGISTRY_HOST = '516175389011.dkr.ecr.ap-northeast-2.amazonaws.com'
                    
                    def imageTag = env.BUILD_NUMBER
                    def ecrImageUri = "${ECR_REGISTRY_HOST}/${env.IMAGE_NAME}:${imageTag}"
                    
                    // 디버깅 로그 추가
                    echo "Guaranteed ECR Region: ${ECR_REGION}"
                    echo "Guaranteed ECR Registry: ${ECR_REGISTRY_HOST}"
                    
                    // 1. ECR 로그인
                    // 정의된 지역 변수 ECR_REGISTRY_HOST를 사용하도록 명확히 수정
                    sh "aws ecr get-login-password --region ${ECR_REGION} | docker login --username AWS --password-stdin ${ECR_REGISTRY_HOST}"
                    
                    // 2. Docker 이미지 빌드
                    sh "docker build -t ${ecrImageUri} ${BACKEND_PATH}"
                    
                    // 3. ECR Push
                    sh "docker push ${ecrImageUri}"
                    
                    echo "ECR Push 완료: ${ecrImageUri}"
                }
            }
        }

        stage('Prepare CodeDeploy Artifact') {
            steps {
                echo "--- CodeDeploy 배포 아티팩트 준비 ---"
                script {
                    // 명시적 경로 재설정
                    def BACKEND_PATH = 'recipe'
                    def imageTag = env.BUILD_NUMBER
                    def ECR_REGISTRY_HOST = '516175389011.dkr.ecr.ap-northeast-2.amazonaws.com'
                    def IMAGE_NAME = 'recipe-app'
                    def ecrImageUri = "${ECR_REGISTRY_HOST}/${IMAGE_NAME}:${imageTag}"

                    // 1. ECR 이미지 URI 파일을 WORKSPACE 루트에 생성 (CodeDeploy용)
                    // CodeDeploy 스크립트에서 Tag만 필요할 수 있으므로 Tag 파일과 URI 파일 모두 생성
                    writeFile file: 'ECR_IMAGE_VALUE.txt', text: ecrImageUri
                    writeFile file: 'image_tag.txt', text: imageTag
                    echo "ECR_IMAGE_VALUE.txt 및 image_tag.txt 생성 완료."

                    // 2. CodeDeploy 아티팩트 복사 및 정리
                    sh "cp -r ${BACKEND_PATH}/scripts ."
                    sh "cp ${BACKEND_PATH}/build/libs/app.jar ."
                    
                    // 3. 현재 작업 공간의 모든 필요한 파일을 zip으로 묶습니다.
                    // (appspec.yml 파일이 Git Root에 있다고 가정합니다.)
                    sh "zip -r deployment.zip scripts app.jar appspec.yml ECR_IMAGE_VALUE.txt image_tag.txt"
                    echo "배포 아티팩트 deployment.zip 생성 완료."
                }
            }
        }

        stage('Upload to S3 and Deploy') {
            steps {
                echo "--- S3 업로드 및 CodeDeploy 배포 시작 ---"
                
                // CodeDeploy 호출 (Jenkins CodeDeploy Plugin 사용)
                awsCodeDeploy appName: env.CODE_DEPLOY_APP, 
                              deploymentGroupName: env.CODE_DEPLOY_GROUP, 
                              file: 'deployment.zip', 
                              s3Bucket: env.S3_BUCKET,
                              s3Prefix: 'deploy-artifacts',
                              wait: true // 배포 결과를 기다림
            }
        }
    }
}
