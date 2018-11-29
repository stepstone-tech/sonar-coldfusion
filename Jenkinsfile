pipeline {
    agent any
    stages {
        stage('Clean') {
           steps {
               echo 'Clean target.'
               sh 'mvn clean'
           }
        }
        stage('Build') { 
            steps { 
               echo 'Package plugin'
               sh 'mvn package'
            }
        }
        stage('Release') {
             steps {
                 echo 'Release plugin to GitHub'
                 sh 'mvn de.jutzig:github-release-plugin:1.3.0:release'
             }
        }
    }
}
