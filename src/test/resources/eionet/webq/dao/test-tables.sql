CREATE TABLE IF NOT EXISTS USER_XML(
id identity primary key,
user_id varchar2(255),
file_name varchar2(255),
xml_schema varchar2(255),
xml blob,
file_size_in_bytes bigint,
created datetime default current_timestamp,
updated datetime default current_timestamp);

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
  new_xml_file_name varchar2(255),
  empty_instance_url varchar2(255),
  file_content blob,
  xml_schema varchar2(2000),
  description varchar2(2000),
  user_name varchar2(2000),
  active boolean,
  main_form boolean,
  created datetime default current_timestamp,
  updated datetime default current_timestamp);
