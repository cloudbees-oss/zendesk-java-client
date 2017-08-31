pipeline {
    agent {
        label "standard"
    }
    stages {
        stage("Build") {
            steps {
                withCredentials(
                        [string(credentialsId: 'ZENDESK_JAVA_CLIENT_TEST_URL', variable: 'ZENDESK_JAVA_CLIENT_TEST_URL'),
                         string(credentialsId: 'ZENDESK_JAVA_CLIENT_TEST_USERNAME', variable: 'ZENDESK_JAVA_CLIENT_TEST_USERNAME'),
                         string(credentialsId: 'ZENDESK_JAVA_CLIENT_TEST_PASSWORD', variable: 'ZENDESK_JAVA_CLIENT_TEST_PASSWORD'),
                         string(credentialsId: 'ZENDESK_JAVA_CLIENT_TEST_TOKEN', variable: 'ZENDESK_JAVA_CLIENT_TEST_TOKEN'),
                         string(credentialsId: 'ZENDESK_JAVA_CLIENT_TEST_REQUESTER.EMAIL', variable: 'ZENDESK_JAVA_CLIENT_TEST_REQUESTER.EMAIL'),
                         string(credentialsId: 'ZENDESK_JAVA_CLIENT_TEST_REQUESTER.NAME', variable: 'ZENDESK_JAVA_CLIENT_TEST_REQUESTER.NAME')]) {
                    withMaven(
                            mavenOpts: '-Xmx512m -Djava.awt.headless=true'
                    ) {
                        sh "mvn clean verify -Dmaven.test.failure.ignore=true"
                    }
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