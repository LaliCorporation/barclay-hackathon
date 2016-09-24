url for postman collection:

https://www.getpostman.com/collections/ceee7f629dca50716f57


database name : registry

create table query :


create table botregistry(
id int AUTO_INCREMENT PRIMARY KEY,
mobile varchar(20) NOT NULL UNIQUE KEY,
type varchar(100),
botname varchar(255),
seqid varchar(255),
otp varchar(50),
isValid boolean DEFAULT false
);