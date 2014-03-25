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

###### 1. Do:
```sh
$ cp config/application-template.properties config/local/application.properties
$ cp config/log4j-template.xml config/local/log4j.xml
```
Set all properties to your environment. Please _**do not commit**_ your local properties.

###### 2. Add the first admin user into the database:
```sql
insert into users values ('username', '', 1);
insert into authorities values ('username', 'ADMIN');
```

###### 3. To use EEA's Central Authentication Service (CAS), 
you need to register Eionet certificates in the JVM that runs the Tomcat. A small Java executable that does it, and a README on how to use it can be found here: https://svn.eionet.europa.eu/repositories/Reportnet/CASServer/contrib/installcert

###### 4. Increase the MySql _max_allowed_packet_ variable 
to be able to store larger files than 1MB in database. For example, to set the limit to 16MB do:
```sql
SET GLOBAL max_allowed_packet=16777216;
```

### Build

Build with Maven `mvn -Denv=local clean install`

### Deployment

Copy webq2.war into _webapps_ directory of your Servlet container.
