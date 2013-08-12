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
  id varchar2(255) primary key,
  description varchar2(2000)
)