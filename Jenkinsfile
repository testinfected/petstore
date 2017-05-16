pipeline {
    agent any

    tools {
        jdk 'jdk8'
        maven 'Maven 3.5.0'
        }

    stages {
        stage('Build') {
            steps {
                echo 'Building..'
		sh 'mvn clean install -DskipTests'
            }
        }
        stage('Test') {
            steps {
                echo 'Testing..'
            }
        }
        stage('Deploy') {
            steps {
                echo 'Deploying....'
            }
        }
    }
}

