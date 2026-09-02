pipeline {
    agent any

    tools {
        jdk 'jdk-21'
        maven 'maven-3.9'
    }

    stages {
        stage('Build') {
            steps {
                bat 'mvn clean compile'
            }
        }

        stage('Test') {
            steps {
                bat 'mvn test'
            }
        }
    }

    post {
        always {
            junit '**/target/surefire-reports/*.xml'
        }
    }
}