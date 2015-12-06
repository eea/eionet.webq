Docker deployment example
=========================

The docker-compose.yml will start up the webq to listen on port 8080 in Tomcat7.
It will start a MySQL service with empty tables except for an initial administrator.

To use it you'll have to either build the docker image with Maven in the top directory:

    mvn -Pdocker install

or run a build in this directory:

   docker build -t dockerrepo.eionet.europa.eu:5000/webq2:latest .

The last option downloads the war file from our Continuous Integration server.
If you want to test your own build, then change the Dockerfile and copy the ../target/webq2.war to here.
