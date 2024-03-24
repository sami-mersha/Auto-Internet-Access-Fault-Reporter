show databases;
create database siinqee;
use siinqee;

create table admin(
	ad_username varchar(10) primary key,
    ad_password varchar(40),
    ad_firstname varchar(40) not null,
    ad_lastname varchar(40),
    ad_sex varchar(1),
    ad_role varchar(40),
    ad_office varchar(40),
    ad_phonenumber int unique,
    ad_email varchar(40),
    ad_session varchar(10)
);

insert into admin values('root', '12345678', 'root', 'account', 'M', "Software Administrator", "Head Office", 0900000000, 'admin@email.com', 'Passive');
update siinqee.admin set ad_session = 'Passive' where ad_username = "root";
select * from admin;

create table branch(
	br_code int primary key,
    br_name varchar(30),
	br_country varchar(20),
    br_region varchar(20),
    br_zone varchar(30),
    br_woreda varchar(30),
    br_city varchar(30),
    br_kebele varchar(30)
);
insert into branch values(1, "Head Office", "Ethiopia", "Addis Ababa", "Addis Ababa", "Kirkos", "Addis Ababa", "Kazanchis");
select * from siinqee.branch;
alter table siinqee.branch
add br_ipaddress varchar(40) unique;
update siinqee.branch set br_ipaddress = '176.36.8.2' where br_code = 1;

create table client(
	cl_code int primary key,
    cl_name varchar(40),
    cl_computername varchar(40),
    cl_ipaddress varchar(40) unique,
    cl_branchcode int,
    cl_session varchar(10),
    foreign key(cl_branchcode) references branch(br_code) on delete cascade
);
--- alter table client drop column cl_lastdate; 
alter table client
add cl_lastseen varchar(35);

insert into client values(1, "Client Name", "Client PC", "172.16.5.1", 1, "Dormant", "Wed Feb 22 23:59:00 EAT 2023");
insert into client values(3, "Abi Yakob", "HP PC", "172.16.5.2", 1, "Dormant", "Wed Feb 22 23:59:00 EAT 2023");
insert into client values(4, "Biniam Alemayehu", "Desktop PC", "172.16.5.3", 1, "Dormant", "Wed Feb 22 23:59:00 EAT 2023");
update siinqee.client set cl_session = 'Active', cl_lastseen = 'Wed Feb 22 23:59:00 EAT 2023' where cl_code = 1;
update siinqee.client set cl_session = 'Active' where cl_code = 1;
select * from siinqee.client;
delete from siinqee.client where cl_code = 2;

create table message(
	me_code int primary key,
    me_type varchar(30),
    me_description varchar(100),
    me_clientcode int, 
    foreign key(me_clientcode) references client(cl_code) on delete cascade
);
insert into message values(1, "Speed Issue", "Kompiitarri koo akka durii saffisaan hojjechaa hin jiru.", 1);

create table history(
	his_code int primary key,
    his_type varchar(15),
    his_detail varchar(100),
    his_id varchar(20),
    his_date varchar(10)
);

insert into history values(1, 'Login Session', 'New Login', 'root', '2023-2-6');
insert into history values(2, 'Registeration', 'New Registeration', 'client-PC', '2023-2-6');
select * from siinqee.history;