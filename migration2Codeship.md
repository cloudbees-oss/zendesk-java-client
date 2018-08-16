## Migration to Codeship

It is a temporal file to track the migration steps of moving this project from Jenkins D@C to Codeship Basic project

**Notes**:

* Codeship currently does’t trigger builds for PRs from forked repositories. We have created a RFE for that [PROD-1600](https://cloudbees.atlassian.net/browse/PROD-1600)
* Sonar configuration needs to be move from the `Manage Jenkins > Configure system` to `Codeship Basic > Enviroment Section`
* Maven options has been moved to Setup Commands `export MAVEN_OPTS="-Xmx512m -Djava.awt.headless=true"`
* Sonar configuration is passed by parameters: https://docs.sonarqube.org/display/SONAR/Analysis+Parameters
* Replacing Jenkins `env.BRANCH_NAME` for Codeship `CI_BRANCH`
* Pushing a first commit to your repository to trigger your first build in Codeship
* All groovy needs to be converted into shell format
* Shell format multiline is not supported
* To check line 13 `_ bash: [: too many arguments`
* BUILD SUCCESS but with mvn `verify` goal default

### Setup Commands

```
# We currently support OpenJDK 7, as well as Oracle JDK 7 & 8.
jdk_switcher use oraclejdk8
# Maven
export MAVEN_OPTS="-Xmx512m -Djava.awt.headless=true"
```

### Pipeline

```
export pom_version=$(xmllint --xpath '/*[local-name()="project"]/*[local-name()="version"]/text()' pom.xml)
export isSnapshot=false
export deployOrVerity='verify'
if [ "$pom_version" =~ "-SNAPSHOT" ]; then isSnapshot=true; fi
if [ [ "$CI_BRANCH" = "master" ] && [ isSnapshot ] ]; then deployOrVerity='deploy -DdeployAtEnd=true'; fi
## DEBUG
echo "Running: mvn clean org.jacoco:jacoco-maven-plugin:prepare-agent ${deployOrVerity} sonar:sonar -Dsonar.organization=cloudbees -Dsonar.branch.name=${CI_BRANCH} -Dsonar.host.url=${SONAR_URL} -Dsonar.login=${SONAR_TOKEN} -Dmaven.test.failure.ignore=true"
## END DEBUG
mvn clean org.jacoco:jacoco-maven-plugin:prepare-agent ${deployOrVerity} sonar:sonar -Dsonar.organization=cloudbees -Dsonar.branch.name=${CI_BRANCH} -Dsonar.host.url=${SONAR_URL} -Dsonar.login=${SONAR_TOKEN} -Dmaven.test.failure.ignore=true
```

### Enviroment Variables: 

* `SONAR_URL`
* `SONAR_TOKEN`