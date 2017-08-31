pipeline {
    agent {
        label "standard"
    }
    stages {
        stage("Build") {
            steps {
                withMaven(
                    mavenOpts: '-Xmx512m -Djava.awt.headless=true'
                ) {
                    sh "mvn clean verify -Dmaven.test.failure.ignore=true"
                }
            }
        }
    }
    options {
        // Keep 10 builds at a time
        buildDiscarder(logRotator(numToKeepStr: '10'))
        // Be sure that this build doesn't hang forever
        timeout(time: 5, unit: 'MINUTES')
    }
}