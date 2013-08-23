-- SET GLOBAL max_allowed_packet = 10485760; -- 10 MB

-- DROP TABLE USER_XML;
-- DROP TABLE PROJECT_FOLDER;
-- DROP TABLE PROJECT_FILE;

CREATE TABLE IF NOT EXISTS USER_XML(
  id int AUTO_INCREMENT,
  user_id varchar(100) CHARACTER SET utf8,
  file_name varchar(255) CHARACTER SET utf8,
  xml_schema varchar(255) CHARACTER SET utf8,
  xml mediumblob,
  file_size_in_bytes bigint,
  created timestamp default current_timestamp,
  updated timestamp,
  primary key (id));

CREATE TABLE IF NOT EXISTS PROJECT_FOLDER(
  id int AUTO_INCREMENT,
  project_id varchar(255) CHARACTER SET utf8 unique,
  description varchar(1000) CHARACTER SET utf8,
  created timestamp default current_timestamp,
  primary key (id));

CREATE TABLE IF NOT EXISTS PROJECT_FILE(
  id int AUTO_INCREMENT,
  project_id int,
  title varchar(255) CHARACTER SET utf8,
  file_name varchar(255) CHARACTER SET utf8,
  file_size_in_bytes bigint,
  new_xml_file_name varchar(255) CHARACTER SET utf8,
  empty_instance_url varchar(255) CHARACTER SET utf8,
  file_content mediumblob,
  xml_schema varchar(255) CHARACTER SET utf8,
  description varchar(1000) CHARACTER SET utf8,
  user_name varchar(50) CHARACTER SET utf8,
  active boolean,
  main_form boolean,
  created timestamp default current_timestamp,
  updated timestamp,
  primary key (id));
