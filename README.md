# Stash
> JSON Parser & Memo Storage Application

**visit here** : http://json-stash.com

<img width="542" alt="스크린샷 2025-01-31 오후 7 50 30" src="https://github.com/user-attachments/assets/189d2d07-2823-4e8b-a5fe-3c55cfe6fdae" />

---

### 프로젝트 개요

- 업무 처리 시 API 조회 결과 로그를 JSON 파싱해야 하는 경우가 많음.
- 여러 API 조회 결과를 조합해 확인하거나, 외부로 전달해야 하는 경우가 많음.
- 위 요구사항을 해결하는 프로젝트를 웹 페이지로 만들어 브라우저 홈페이지로 활용.

### 주요 기능

- **JSON 데이터 저장 기능**: 파싱된 JSON 데이터를 메모와 함께 저장, 불러오기
- **메모 관리 기능**: 간단한 메모 저장, 불러오기
- **회원 기반 데이터 관리**: JSON 데이터, 메모 데이터를 로그인 ID 기반으로 관리

### 기술 스택

**Springboot**, **Spring Security**, **OpenAPI Generator**, **Java**

### 프로젝트 아키텍처

<details>
<summary>아키텍처 설명 접기/펼치기</summary>

#### AWS Service
| 요소       | 사용 목적 |
|------------|--------------------------------|
| **FE EC2**  | Nuxt3 |
| **BE EC2**   | Spring Boot |
| **Jenkins EC2** | CI/CD 서버 |
| **RDS** | MySQL DB |
| **S3**  | 정적 파일 저장 & OpenAPI YAML 파일 저장소 |


#### 네트워크 및 보안 그룹 설정
VPC (FE, BE, Jenkins EC2 포함)
보안 그룹 설정
| 요소       | 보안 설정 |
|------------|--------------------------------|
| **FE EC2**  | 80 포트: 공개 (ANY), 22 포트 (SSH): Jenkins EC2에서만 허용 |
| **BE EC2**   | 8080 포트: FE EC2에서만 허용, 22 포트 (SSH): Jenkins EC2에서만 허용 |
| **Jenkins EC2** | 80 포트: 개발자 IP에서만 허용 |
| **RDS** | 3306 포트: BE EC2에서만 허용 |
</details>


<details>
<summary>Jenkins CI/CD Pipeline 접기/펼치기</summary>

```
pipeline {
    agent any
    environment {
        DB_USERNAME = "???"
        DB_PASSWORD = "???"
        DB_HOST = "???"
        DB_PORT = "3306"
        DB_NAME = "???"
        AWS_REGION = 'ap-northeast-2'
        REPO_URL = 'https://github.com/Myunwoo/stash_backend.git'
        BRANCH_NAME = 'main'
        EC2_TARGET = "???"
        APP_DIR = '/home/ubuntu/stash-backend'
        OAG_DIR = '/var/lib/jenkins/workspace/stash_backend_deploy/src/main/resources/openapi_stash'
        S3_BUCKET = "???"
    }
    stages {
        stage('Checkout Source') {
            steps {
                echo 'Checking out source code...'
                git branch: "${BRANCH_NAME}", url: "${REPO_URL}"
            }
        }
        stage('Build Application') {
            steps {
                echo 'Building Spring Boot application...'
                sh """
                ./gradlew clean build \
                -Dspring.datasource.url=jdbc:mysql://${DB_HOST}:${DB_PORT}/${DB_NAME} \
                -Dspring.datasource.username=${DB_USERNAME} \
                -Dspring.datasource.password=${DB_PASSWORD}
                """
            }
        }
        stage('Upload to S3') {
            steps {
                echo 'Uploading specific files and directories to S3...'
                sh """
                aws s3 cp ${OAG_DIR} s3://${S3_BUCKET} --recursive --region ${AWS_REGION}
                """
            }
        }
        stage('Deploy to EC2') {
            steps {
                echo 'Deploying to EC2...'
                withCredentials([sshUserPrivateKey(credentialsId: 'backend-deploy-ssh-key', keyFileVariable: 'SSH_KEY')]) {
                    sh '''
                    echo "Creating remote directory..."
                    ssh -o StrictHostKeyChecking=no -i $SSH_KEY ${EC2_TARGET} "mkdir -p ${APP_DIR}"

                    echo "Transferring build files..."
                    scp -o StrictHostKeyChecking=no -i $SSH_KEY build/libs/*.jar ${EC2_TARGET}:${APP_DIR}/app.jar

                    // echo "Restarting the application..."
                    // ssh -o StrictHostKeyChecking=no -i $SSH_KEY ${EC2_TARGET} "nohup java -jar nohup java -jar ${APP_DIR}/app.jar/stash-0.0.1-SNAPSHOT.jar > ${APP_DIR}/app.log 2>&1 &"

                    echo "Restarting the application..."
                    ssh -o StrictHostKeyChecking=no -i $SSH_KEY ${EC2_TARGET} "
                        pkill -f 'stash-0.0.1-SNAPSHOT.jar' || true
                        nohup java -jar ${APP_DIR}/app.jar/stash-0.0.1-SNAPSHOT.jar > ${APP_DIR}/app.log 2>&1 &
                    "
                    '''
                }
            }
        }
    }
    post {
        success {
            echo 'Pipeline completed successfully.'
        }
        failure {
            echo 'Pipeline failed. Please check the logs for more details.'
        }
    }
}
```
</details>

![스크린샷 2025-01-31 오후 10 49 12](https://github.com/user-attachments/assets/c8bd8621-ee88-4ab7-bc91-7f57893b161a)

