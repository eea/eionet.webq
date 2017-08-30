FROM tomcat:latest
RUN rm -rf /usr/local/tomcat/conf/logging.properties /usr/local/tomcat/webapps/*
COPY target/webq2.war /usr/local/tomcat/webapps/ROOT.war
