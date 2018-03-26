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
* Java 8
* Maven 3.3.9
* Tomcat 8.5.20
* MySql 5.5

### Setup

WebQ is configured at runtime with system properties. These can be provided to Tomcat via the CATALINA_OPTS environment variable.

###### 1. Do:
Set all properties needed for your environment. These are the properties you can change with their default values:
```
db.driver=com.mysql.jdbc.Driver
db.url=jdbc:mysql://webqdb/webq2?maxAllowedPacket=32212254720
db.username=
db.password=
cas.service=http://localhost:8080/webq2
cas.server.host=https://sso.eionet.europa.eu
user.file.expiration.hours=12
converters.api.url=
log4j.configuration=classes/log4j.xml
initial.admin.username=
initial.admin.password=
```

###### 2. Add the first admin user into the database:
If you set the initial.admin.username system property, then the user will get the admin role at startup.

###### 3. Increase the MySql _max_allowed_packet_ variable 
To be able to store larger files than 1MB in the database. For example, to set the limit to 16MB in /etc/my.cnf do:
```sql
SET GLOBAL max_allowed_packet=16777216;
```
or run the mysql executable with the `--max_allowed_packet=16M` argument.

### Build

Build with Maven `mvn clean install`