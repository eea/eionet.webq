CREATE TABLE IF NOT EXISTS USER_XML(
id identity primary key,
user_id varchar2(255),
file_name varchar2(255),
xml_schema varchar2(255),
file_content_id bigint,
file_size_in_bytes bigint,
created datetime default current_timestamp,
updated datetime,
downloaded datetime);

CREATE TABLE IF NOT EXISTS PROJECT_FOLDER(
  id identity primary key,
  project_id varchar2(255) unique,
  description varchar2(2000),
  created datetime default current_timestamp);

CREATE TABLE IF NOT EXISTS PROJECT_FILE(
  id identity primary key,
  project_id bigint,
  title varchar2(255),
  file_name varchar2(255),
  file_size_in_bytes varchar2(255),
  remote_file_url varchar2(500),
  new_xml_file_name varchar2(255),
  empty_instance_url varchar2(255),
  file_content_id bigint,
  file_type varchar(255),
  xml_schema varchar2(2000),
  description varchar2(2000),
  user_name varchar2(2000),
  active boolean,
  main_form boolean,
  created datetime default current_timestamp,
  updated datetime default current_timestamp);

CREATE UNIQUE INDEX IF NOT EXISTS unique_project_file_name ON project_file(project_id, file_name);


CREATE TABLE IF NOT EXISTS FILE_CONTENT (
  id           identity primary key,
  file_content blob
);
CREATE TABLE IF NOT EXISTS users(
  username varchar2(255) unique,
  password varchar2(255),
  enabled boolean);

CREATE TABLE IF NOT EXISTS authorities(
  username varchar2(255),
  authority varchar2(255),
  constraint fk_authorities_users foreign key(username) references users(username));

CREATE UNIQUE INDEX IF NOT EXISTS unique_username_authority ON authorities(username,authority);