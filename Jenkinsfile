// For Reference Only
pipeline {
    environment {
        ZENDESK_JAVA_CLIENT_TEST_URL             = credentials('ZENDESK_JAVA_CLIENT_TEST_URL')
        ZENDESK_JAVA_CLIENT_TEST_USERNAME        = credentials('ZENDESK_JAVA_CLIENT_TEST_USERNAME')
        ZENDESK_JAVA_CLIENT_TEST_PASSWORD        = credentials('ZENDESK_JAVA_CLIENT_TEST_PASSWORD')
        ZENDESK_JAVA_CLIENT_TEST_TOKEN           = credentials('ZENDESK_JAVA_CLIENT_TEST_TOKEN')
        ZENDESK_JAVA_CLIENT_TEST_REQUESTER_EMAIL = credentials('ZENDESK_JAVA_CLIENT_TEST_REQUESTER.EMAIL')
        ZENDESK_JAVA_CLIENT_TEST_REQUESTER_NAME  = credentials('ZENDESK_JAVA_CLIENT_TEST_REQUESTER.NAME')
    }
    agent {
        label "standard"
    }
    stages {
        stage("Build") {
            steps {
                withSonarQubeEnv('sonarcloud.io') {
                    withMaven(
                            mavenOpts: '-Xmx512m -Djava.awt.headless=true'
                    ) {
                        sh "mvn clean org.jacoco:jacoco-maven-plugin:prepare-agent ${env.BRANCH_NAME == 'master' && readMavenPom().version.contains('-SNAPSHOT') ? 'deploy -DdeployAtEnd=true' : 'verify'} sonar:sonar -Dsonar.organization=cloudbees -Dsonar.branch.name=\"${env.BRANCH_NAME}\" -Dmaven.test.failure.ignore=true"
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
