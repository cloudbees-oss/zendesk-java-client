h3. Migration to Codeship

It is a temporal file to track the migration steps od moving this project from Jenkins D@C to Codeship Basic project

h4. Carlos (16/08/2018)

* Sonar configuration needs to be move from the `Manage Jenkins > Configure system` to `Codeship Basic > Enviroment Section`
* Maven options has been moved to Setup Commands `export MAVEN_OPTS="-Xmx512m -Djava.awt.headless=true"`
* Sonar configuration is passed by parameters: https://docs.sonarqube.org/display/SONAR/Analysis+Parameters
* Pushing a first commit to your repository to trigger your first build in Codeship