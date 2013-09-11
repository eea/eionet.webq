-- SET GLOBAL max_allowed_packet = 10485760; -- 10 MB
SET NAMES utf8;

-- DROP TABLE USER_XML;
-- DROP TABLE PROJECT_FOLDER;
-- DROP TABLE PROJECT_FILE;

CREATE TABLE IF NOT EXISTS USER_XML(
  id int AUTO_INCREMENT,
  user_id varchar(100),
  file_name varchar(255),
  xml_schema varchar(255),
  xml mediumblob,
  file_size_in_bytes bigint,
  created timestamp default current_timestamp,
  updated timestamp,
  downloaded timestamp,
  primary key (id)) DEFAULT CHARACTER SET utf8;

CREATE TABLE IF NOT EXISTS PROJECT_FOLDER(
  id int AUTO_INCREMENT,
  project_id varchar(255) unique,
  description varchar(1000),
  created timestamp default current_timestamp,
  primary key (id)) DEFAULT CHARACTER SET utf8;

CREATE TABLE IF NOT EXISTS PROJECT_FILE(
  id int AUTO_INCREMENT,
  project_id int,
  title varchar(255),
  file_name varchar(255),
  file_size_in_bytes bigint,
  file_type varchar(255),
  remote_file_url varchar(500),
  new_xml_file_name varchar(255),
  empty_instance_url varchar(255),
  file_content mediumblob,
  xml_schema varchar(255),
  description varchar(1000),
  user_name varchar(50),
  active boolean,
  main_form boolean,
  created timestamp default current_timestamp,
  updated timestamp,
  primary key (id),
  unique index project_to_file_name (project_id, file_name)) DEFAULT CHARACTER SET utf8;

CREATE TABLE IF NOT EXISTS users(
  username varchar(255) unique,
  password varchar(255),
  enabled boolean) DEFAULT CHARACTER SET utf8;

CREATE TABLE IF NOT EXISTS authorities(
  username varchar(255),
  authority varchar(255),
  constraint fk_authorities_users foreign key(username) references users(username),
  unique index ix_auth_username (username,authority)) DEFAULT CHARACTER SET utf8;
