pipeline {
  agent { label 'docker-agent' }

  environment {
    IMAGE_NAME = "demo-micro-jenkins"
    DOCKERHUB_NAMESPACE = "evelynandrade"         
    REGISTRY = "docker.io"
    JAVA_HOME = tool name: 'JDK17', type: 'hudson.model.JDK'
    MAVEN_HOME = tool name: 'M3', type: 'hudson.tasks.Maven$MavenInstallation'
    PATH = "${JAVA_HOME}/bin:${MAVEN_HOME}/bin:${env.PATH}"
  }

  options {
    timestamps()
    ansiColor('xterm')
    disableConcurrentBuilds()
  }

  stages {
    stage('Checkout') {
      steps {
        checkout scm
      }
    }

    stage('Build JAR') {
      steps {
        sh 'mvn -B -DskipTests clean package'
      }
      post {
        success {
          archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
        }
      }
    }

stage('Build & Push Image') {
      steps {
        script {
          // --- DEFINICIÓN DE VARIABLES ---
          def tag = env.BUILD_NUMBER
          def FULL_IMAGE_NAME = "${DOCKERHUB_NAMESPACE}/${IMAGE_NAME}"
          
          def IMAGE_WITH_TAG = "${FULL_IMAGE_NAME}:${tag}" 
          def IMAGE_LATEST = "${FULL_IMAGE_NAME}:latest" 
          // -------------------------------
          
          // 1. BUILD: Usa la variable corregida IMAGE_WITH_TAG
          sh "/usr/bin/docker build -t ${IMAGE_WITH_TAG} ."

          // 2. TAG: Usa ambas variables
          sh "/usr/bin/docker tag ${IMAGE_WITH_TAG} ${IMAGE_LATEST}"

          // 3 PUSH: 
          withCredentials([usernamePassword(credentialsId: 'dockerhub-creds', 
                                           usernameVariable: 'DOCKER_USERNAME', 
                                           passwordVariable: 'DOCKER_PASSWORD')]){
              
              sh "/usr/bin/docker login -u ${DOCKER_USERNAME} -p ${DOCKER_PASSWORD}"
              
              // Push Tagged (Usamos IMAGE_WITH_TAG)
              sh "/usr/bin/docker push ${IMAGE_WITH_TAG}" 
              
              // Push Latest (Usamos IMAGE_LATEST)
              sh "/usr/bin/docker push ${IMAGE_LATEST}"
          }
        }
      }
    }
  }
    
  post {
    success {
      echo "Imagen publicada: ${env.REGISTRY}/${DOCKERHUB_NAMESPACE}/${IMAGE_NAME}:${env.BUILD_NUMBER}"
    }
    failure {
      echo "Build fallido. Revisar logs."
    }
  }
}
