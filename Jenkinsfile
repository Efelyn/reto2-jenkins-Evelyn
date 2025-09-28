pipeline {
  agent { label 'docker-agent' }

  environment {
    IMAGE_NAME = "demo-micro"
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
          def tag = env.BUILD_NUMBER
          def FULL_IMAGE_NAME = "${DOCKERHUB_NAMESPACE}/${IMAGE_NAME}"

          // 1. BUILD: Usamos la ruta absoluta para que encuentre el binario Docker.
          sh "/usr/bin/docker build -t ${FULL_IMAGE_NAME}:${tag} ."

          // 2. PUSH: Nos autenticamos y subimos usando la ruta absoluta.
          withCredentials([string(credentialsId: 'dockerhub-creds', variable: 'DOCKER_PASSWORD')]) {
              // Login
              sh "/usr/bin/docker login -u ${env.DOCKERHUB_NAMESPACE} -p ${DOCKER_PASSWORD}"
              // Push Tagged
              sh "/usr/bin/docker push ${FULL_IMAGE_NAME}:${tag}"
              // Push Latest
              sh "/usr/bin/docker push ${FULL_IMAGE_NAME}:latest"
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
