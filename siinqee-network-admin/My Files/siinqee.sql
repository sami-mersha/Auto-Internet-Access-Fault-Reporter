show databases;
create database siinqee;
use siinqee;

create table agent(
	ag_username varchar(10) primary key,
    ag_password varchar(40),
    ag_firstname varchar(40) not null,
    ag_lastname varchar(40),
    ag_dob date,
    ag_sex varchar(1),
    ag_address int,
    ag_phonenumber int unique,
    ag_branch varchar(40),
    ag_role varchar(40)
);

insert into agent values('samimersha', '11112222', 'Samuel', 'Mersha', '1999-11-27', 'M', 1, 0901685340, 'Finfinnee', 'IT Network');
select * from agent;

create table history(
	his_code int primary key,
    his_type varchar(15),
    his_detail varchar(100),
    his_username varchar(10),
    his_date varchar(10)
);

insert into history values(1, 'Login Session', 'Active', 'samimersha', '2023-2-6');
select his_username from siinqee.history where his_code = 1;
update siinqee.history set his_detail = 'Passive', his_username = 'mersha' where his_code = 1;
select * from siinqee.history;



