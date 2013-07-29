CREATE TABLE IF NOT EXISTS USER_XML(
id bigint auto_increment primary key,
session_id varchar2(255),
fileName varchar2(255),
xml_schema varchar2(255),
xml nclob,
created datetime);