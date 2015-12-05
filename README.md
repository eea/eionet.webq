Eionet WebQuestionnaires 2
==========================

Introduction
------------
The system lets anonymous users to create and edit XML files by using webforms. 
The webforms are implemented in the XForms language and they are maintained in the system.

Main Features
-------------
* Create XML file using predefined webforms
* Upload XML and edit it with webform
* Download changed XML file
* Download changed XML file in other human readable formats
* Developers can maintain the webforms in Webforms repository

Installation
------------

### Prerequisites

Minimum requirements are:
* Java 1.6
* Maven 3.0.2
* Tomcat 6
* MySql 5.5

### Setup

WebQ is configured at runtime with system properties. These can be provided to Tomcat via the CATALINA_OPTS environment variable.

###### 1. Do:
Set all properties needed for your environment. These are the properties you can change with their default values:
```
db.driver=com.mysql.jdbc.Driver
db.url=jdbc:mysql://webqdb/webq2?maxAllowedPacket=30M
db.username=
db.password=
cas.service=http://localhost:8080/webq2
cas.server.host=https://sso.eionet.europa.eu
user.file.expiration.hours=12
converters.api.url=
webq1.url=
log4j.configuration=classes/log4j.xml
initial.admin.username=
initial.admin.password=
```

###### 2. Add the first admin user into the database:
```sql
insert into users values ('username', '', 1);
insert into authorities values ('username', 'ADMIN');
```

###### 3. To use EEA's Central Authentication Service (CAS), 
If you deploy on Tomcat 6, then you need to register Eionet certificates in the JVM that runs the Tomcat. A small Java executable that does it, and a README on how to use it can be found here: https://svn.eionet.europa.eu/repositories/Reportnet/CASServer/contrib/installcert

###### 4. Increase the MySql _max_allowed_packet_ variable 
To be able to store larger files than 1MB in the database. For example, to set the limit to 16MB in /etc/my.cnf do:
```sql
SET GLOBAL max_allowed_packet=16777216;
```
or run the mysql executable with the `--max_allowed_packet=16M` argument.

### Build

Build with Maven `mvn clean install`

### Deployment

Copy webq2.war into _webapps_ directory of your Servlet container.
