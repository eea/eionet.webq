pipeline {
  agent { node { label 'docker-1.13' } }
  tools {
    maven 'maven3'
    jdk 'Java8'
  }
  options {
    buildDiscarder(logRotator(numToKeepStr: '4', artifactNumToKeepStr: '2'))
    timeout(time: 60, unit: 'MINUTES')
  }
  stages {
    stage('Project Build') {
      steps {
        sh 'mvn clean -B -V -Pcobertura verify cobertura:cobertura pmd:pmd pmd:cpd findbugs:findbugs checkstyle:checkstyle'
      }
      post {
        always {
            junit '**/target/surefire-reports/*.xml'
            pmd canComputeNew: false
            dry canComputeNew: false
            checkstyle canComputeNew: false
            findbugs pattern: '**/findbugsXml.xml'
            openTasks canComputeNew: false
            cobertura coberturaReportFile: '**/target/site/cobertura/coverage.xml', failNoReports: true
        }
      }
    }
  }
}
