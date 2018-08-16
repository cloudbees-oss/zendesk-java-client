h3. Migration to Codeship

It is a temporal file to track the migration steps od moving this project from Jenkins D@C to Codeship Basic project

Notes: Codeship currently does’t trigger builds for PRs from forked repositories. We have created a RFE for that [PROD-1600](https://cloudbees.atlassian.net/browse/PROD-1600)

h4. Carlos (16/08/2018)

* Sonar configuration needs to be move from the `Manage Jenkins > Configure system` to `Codeship Basic > Enviroment Section`
* Maven options has been moved to Setup Commands `export MAVEN_OPTS="-Xmx512m -Djava.awt.headless=true"`
* Sonar configuration is passed by parameters: https://docs.sonarqube.org/display/SONAR/Analysis+Parameters
* Replacing Jenkins `env.BRANCH_NAME` for Codeship `CI_BRANCH`
* Pushing a first commit to your repository to trigger your first build in Codeship
* All groovy needs to be converted into shell format
* Shell format multiline is not supported
* To check line 13 `_ bash: [: too many arguments`
* BUILD SUCCESS but with mvn `verify` goal default