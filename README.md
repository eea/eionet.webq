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

Do `cp config/application-template.properties config/local/application.properties`

Set all properties to your environment.

Please _**do not commit**_ your local properties.

### Build

Build with Maven `mvn clean install`

### Deployment

Copy webq2.war into _webapps_ directory of your Servlet container.
