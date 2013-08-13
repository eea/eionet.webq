CREATE TABLE IF NOT EXISTS USER_XML(
id int AUTO_INCREMENT,
user_id varchar(255) CHARACTER SET utf8,
file_name varchar(255) CHARACTER SET utf8,
xml_schema varchar(255) CHARACTER SET utf8,
xml blob,
file_size_in_bytes bigint,
created datetime default current_timestamp,
updated datetime default current_timestamp,
primary key (id));

CREATE TABLE IF NOT EXISTS PROJECT_FOLDER(
  id varchar(255) CHARACTER SET utf8,
  description varchar(2000) CHARACTER SET utf8,
  primary key (id)
)