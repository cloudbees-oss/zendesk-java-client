node {
    step([$class: 'GitHubSetCommitStatusBuilder'])

    // Checkout code
    stage 'Checkout'
    checkout scm
    def pom = readMavenPom()
    def projectName = pom.name
    def projectVersion = pom.version

    // Build & test
    stage 'Build'
    try {
        echo "Building ${projectName} - ${projectVersion}"
        mvn "-Dmaven.test.failure.ignore clean verify"
        step([$class: 'ArtifactArchiver', artifacts: '**/target/*.jar', fingerprint: true])
        step([$class: 'JUnitResultArchiver', testResults: '**/target/surefire-reports/*.xml'])
        step([$class: 'FindBugsPublisher', pattern: '**/findbugsXml.xml'])
        currentBuild.result = 'SUCCESS'
    } catch (Exception err) {
        currentBuild.result = 'FAILURE'
    }

    // Notifications
    stage 'Notify'
    step([$class: 'GitHubCommitNotifier', resultOnFailure: 'FAILURE'])
    def color = 'GREEN'
    if (!isOK()) {
        color = 'RED'
    }
    hipchatSend color: "${color}",
        message: "${projectName} - ${projectVersion} @ ${env.BRANCH_NAME} <a href='${env.BUILD_URL}'>#${env.BUILD_NUMBER}</a> status: ${currentBuild.result}",
        room: 'support-room'
}

// Utility functions

def mvn(String goals) {
    def mvnHome = tool "maven-3.3.9"
    def javaHome = tool "oracle-8u74"

    withEnv(["JAVA_HOME=${javaHome}", "PATH+MAVEN=${mvnHome}/bin"]) {
        wrap([$class: 'ConfigFileBuildWrapper', managedFiles: [
            [fileId: 'org.jenkinsci.plugins.configfiles.maven.MavenSettingsConfig1446135612420', targetLocation: "settings.xml", variable: '']
        ]]) {
            sh "mvn -s settings.xml -B ${goals}"
        }
    }
}

@com.cloudbees.groovy.cps.NonCPS
def isOK() {
    return "SUCCESS".equals(currentBuild.result)
}
