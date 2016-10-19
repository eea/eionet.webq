FROM tomcat:7.0.56-jre7
MAINTAINER Søren Roug <soren.roug@eea.europa.eu>

RUN rm -rf /usr/local/tomcat/webapps/*

ADD http://ci.eionet.europa.eu/view/Java/job/WEBQ2/lastSuccessfulBuild/artifact/target/webq2.war /usr/local/tomcat/webapps/ROOT.war
#COPY webq2.war /usr/local/tomcat/webapps/ROOT.war

