drop table if exists ad cascade;
drop table if exists ad_details cascade;
drop table if exists ad_images cascade;
drop table if exists basic_ad_details cascade;
drop table if exists brand cascade;
drop table if exists category cascade;
drop table if exists images cascade;
drop table if exists messages cascade;
drop table if exists rates cascade;
drop table if exists roles cascade;
drop table if exists saved_ads cascade;
drop table if exists type cascade;
drop table if exists userdata cascade;
drop table if exists users cascade;
drop table if exists users_messages cascade;
drop table if exists users_rates cascade;
drop table if exists users_roles cascade;

CREATE TABLE images(
    ID INT NOT NULL AUTO_INCREMENT,
    PATH varchar(255) NOT NULL,
    UPLOADED_TIME TIMESTAMP NOT NULL,
    constraint i_pk primary key (ID),
    constraint i_path_uq unique (PATH)
);

CREATE TABLE users (
    ID INT NOT NULL AUTO_INCREMENT,
    USERNAME varchar(255) NOT NULL,
    PASSWORD varchar(255) NOT NULL,
    EMAIL varchar(255) NOT NULL,
    ENABLED BOOLEAN,
    PROFILE_IMG_ID INT,
    ACTIVATION varchar(255),
    LAST_LOGIN TIMESTAMP,
    CREATED TIMESTAMP NOT NULL,
    constraint u_pk primary key (ID),
    constraint u_username_uq unique (USERNAME),
    constraint u_email_uq unique (EMAIL),
    constraint u_fk_i foreign key (PROFILE_IMG_ID) references images(ID)
);

CREATE TABLE userdata(
    USER_ID INT,
    CITY  varchar(255),
    FULL_NAME  varchar(255),
    PUBLIC_EMAIL  varchar(255),
    PHONE_NUMBER  varchar(255),
    constraint ud_fk_u foreign key (USER_ID) references users(ID),
    constraint ud_pk primary key (USER_ID)
);

CREATE TABLE roles (
    ID INT NOT NULL AUTO_INCREMENT,
    ROLE_NAME  varchar(20) NOT NULL,
    constraint role_pk primary key (ID),
    constraint role_name_uq unique (ROLE_NAME)
);

CREATE TABLE users_roles(
    USER_ID INT,
    ROLE_ID INT,
    constraint ur_fk_u foreign key (USER_ID) references users(ID),
    constraint ur_fk_r foreign key (ROLE_ID) references roles(ID),
    constraint ur_pk primary key (USER_ID,ROLE_ID)
);

CREATE TABLE messages(
    ID INT NOT NULL AUTO_INCREMENT,
    CONTENT varchar(1000) NOT NULL,
    constraint m_pk primary key (ID)
);
CREATE TABLE users_messages(
    MESSAGE_ID INT,
    SENDER_ID INT,
    RECEIVER_ID INT,
    SENT_TIME TIMESTAMP,
    UNREAD BOOLEAN,
    constraint um_fk_m foreign key (MESSAGE_ID) references messages(ID),
    constraint um_fk_s foreign key (SENDER_ID) references users(ID),
    constraint um_fk_r foreign key (RECEIVER_ID) references users(ID),
    constraint um_pk primary key (MESSAGE_ID,SENDER_ID,RECEIVER_ID)
);

CREATE TABLE category(
    ID INT NOT NULL AUTO_INCREMENT,
    NAME  varchar(100) NOT NULL,
    constraint c_pk primary key (ID),
    constraint c_name unique (NAME)
);
CREATE TABLE brand(
    BRAND  varchar(50),
    constraint b_pk primary key (BRAND)
);
CREATE TABLE type(
    ID INT NOT NULL AUTO_INCREMENT,
    BRAND  varchar(50),
    NAME  varchar(50),
    constraint t_fk_b foreign key (BRAND) references brand(BRAND),
    constraint t_pk primary key (ID)
);

CREATE TABLE ad(
    ID  INT NOT NULL AUTO_INCREMENT,
    CREATOR_ID INT,
    CATEGORY_ID INT,
    TYPE_ID INT,
    TITLE  varchar(255) NOT NULL,
    PRICE DOUBLE(10,2) NOT NULL,
    CURRENCY varchar(3) NOT NULL,
    PRODUCT_CONDITION ENUM('USED','NEW','SPARED'),
    STATE ENUM('ACTIVE','ARCHIVED','FROZEN') NOT NULL,
    CREATED TIMESTAMP NOT NULL,
    constraint a_pk primary key (ID),
    constraint a_fk_u foreign key (CREATOR_ID) references users(ID),
    constraint a_fk_c foreign key (CATEGORY_ID) references category(ID),
    constraint a_fk_t foreign key (TYPE_ID) references type(ID)
);
CREATE TABLE ad_images(
    AD_ID INT,
    IMAGE_ID INT,
    constraint ai_fk_ad foreign key (AD_ID) references ad(ID),
    constraint ai_fk_im foreign key (IMAGE_ID) references images(ID),
    constraint aim_pk primary key (AD_ID,IMAGE_ID)
);

CREATE TABLE basic_ad_details(
    AD_ID INT,
    YEAR INT(4),
    BATTERY_SIZE INT(5),
    KM INT(7),
    PERFORMANCE INT(5),
    DRIVE ENUM('AWD','RWD','FWD'),
    SEAT_NUMBER INT(3),
    CHARGE_SPEED INT(6),
    constraint bd_fk_ad foreign key (AD_ID) references ad(ID),
    constraint bd_pk primary key (AD_ID)
);

CREATE TABLE ad_details(
    AD_ID INT,
    DESCRIPTION  varchar(1500),
    PRODUCT_RANGE INT(5),
    WEIGHT INT(6),
    ACCELARATION DOUBLE(5,2),
    MAX_SPEED INT(4),
    COLOR  varchar(25),
    constraint pd_fk_ad foreign key (AD_ID) references ad(ID),
    constraint pd_pk primary key (AD_ID)
);

CREATE TABLE saved_ads(
    USER_ID INT,
    AD_ID INT,
    constraint sa_fk_u foreign key (USER_ID) references users(ID),
    constraint sa_fk_a foreign key (AD_ID) references ad(ID),
    constraint sa_pk primary key (USER_ID,AD_ID)
);

CREATE TABLE rates(
    ID INT NOT NULL AUTO_INCREMENT,
    STATE ENUM('POSITIVE','NEGATIVE'),
    DESCRIPTION  varchar(500),
    CREATED TIMESTAMP,
    constraint r_pk primary key (ID)
);

CREATE TABLE users_rates(
    RATE_ID INT,
    RATING_USER_ID INT,
    RATED_USER_ID INT,
    AD_ID INT,
    STATE ENUM('SELLER','BUYER'),
    STATUS ENUM('OPEN','CLOSED'),
    ACTIVATION_CODE  varchar(255),
    constraint urates_fk_r foreign key (RATE_ID) references rates(ID),
    constraint urates_rating_u_fk foreign key (RATING_USER_ID) references users(ID),
    constraint urates_rated_u_fk foreign key (RATED_USER_ID) references users(ID),
    constraint urates_fk_a foreign key (AD_ID) references ad(ID),
    constraint urates_pk primary key (RATE_ID,RATING_USER_ID,RATED_USER_ID)
);

insert into images (PATH, UPLOADED_TIME)
values ('/profiles/default.jpg',sysdate());

insert into users(USERNAME, PASSWORD, EMAIL, ENABLED, ACTIVATION, LAST_LOGIN, CREATED,PROFILE_IMG_ID)
    VALUES ('user1','pass1','email1',true,null,sysdate(),sysdate(),1);
insert into users(USERNAME, PASSWORD, EMAIL, ENABLED, ACTIVATION, LAST_LOGIN, CREATED,PROFILE_IMG_ID)
    VALUES ('user2','pass2','email2',true,null,sysdate(),sysdate(),1);
insert into users(USERNAME, PASSWORD, EMAIL, ENABLED, ACTIVATION, LAST_LOGIN, CREATED,PROFILE_IMG_ID)
    VALUES ('user3','pass3','email3',true,null,sysdate(),sysdate(),1);
insert into users(USERNAME, PASSWORD, EMAIL, ENABLED, ACTIVATION, LAST_LOGIN, CREATED,PROFILE_IMG_ID)
    VALUES ('user4','pass4','email4',true,null,sysdate(),sysdate(),1);
insert into users(USERNAME, PASSWORD, EMAIL, ENABLED, ACTIVATION, LAST_LOGIN, CREATED,PROFILE_IMG_ID)
    VALUES ('user5','pass5','email5',true,null,sysdate(),sysdate(),1);

insert into userdata(USER_ID, CITY, FULL_NAME, PUBLIC_EMAIL, PHONE_NUMBER)
    VALUES ( 1,'city1','fullname1','pemail1','705551286' );
insert into userdata(USER_ID, CITY, FULL_NAME, PUBLIC_EMAIL, PHONE_NUMBER)
    VALUES ( 2,'city2','fullname2','pemail2','705551286' );
insert into userdata(USER_ID, CITY, FULL_NAME, PUBLIC_EMAIL, PHONE_NUMBER)
    VALUES ( 3,'city3','fullname3','pemail3','705551286' );
insert into userdata(USER_ID, CITY, FULL_NAME, PUBLIC_EMAIL, PHONE_NUMBER)
    VALUES ( 4,null,null,null,'705551286' );
insert into userdata(USER_ID, CITY, FULL_NAME, PUBLIC_EMAIL, PHONE_NUMBER)
    VALUES (5,null,'fullname5',null,'705551286' );

insert into roles(ROLE_NAME) values ('USER');
insert into roles(ROLE_NAME) values ('ADMIN');

insert into users_roles(USER_ID, ROLE_ID) VALUES ( 1,2 );
insert into users_roles(USER_ID, ROLE_ID) VALUES ( 2,1 );
insert into users_roles(USER_ID, ROLE_ID) VALUES ( 3,1 );
insert into users_roles(USER_ID, ROLE_ID) VALUES ( 4,1 );
insert into users_roles(USER_ID, ROLE_ID) VALUES ( 5,1 );

insert into messages(CONTENT) VALUES ( 'Message1 content');
insert into messages(CONTENT) VALUES ( 'Message2 content');
insert into messages(CONTENT) VALUES ( 'Message3 content');
insert into messages(CONTENT) VALUES ( 'Message4 content');
insert into messages(CONTENT) VALUES ( 'Message5 content');
insert into messages(CONTENT) VALUES ( 'Message6 content');
insert into messages(CONTENT) VALUES ( 'Message7 content');

insert into users_messages(MESSAGE_ID, SENDER_ID, RECEIVER_ID, SENT_TIME, UNREAD)
    VALUES ( 1,1,2,sysdate(),false);

insert into users_messages(MESSAGE_ID, SENDER_ID, RECEIVER_ID, SENT_TIME, UNREAD)
    VALUES ( 2,2,3,sysdate(),false);

insert into users_messages(MESSAGE_ID, SENDER_ID, RECEIVER_ID, SENT_TIME, UNREAD)
    VALUES ( 3,3,2,sysdate(),false);

insert into users_messages(MESSAGE_ID, SENDER_ID, RECEIVER_ID, SENT_TIME, UNREAD)
    VALUES ( 4,4,5,sysdate(),false);

insert into users_messages(MESSAGE_ID, SENDER_ID, RECEIVER_ID, SENT_TIME, UNREAD)
    VALUES ( 5,5,4,sysdate(),false);

insert into users_messages(MESSAGE_ID, SENDER_ID, RECEIVER_ID, SENT_TIME, UNREAD)
    VALUES ( 6,3,1,sysdate(),false);

insert into users_messages(MESSAGE_ID, SENDER_ID, RECEIVER_ID, SENT_TIME, UNREAD)
    VALUES ( 7,1,5,sysdate(),true);

insert into brand(BRAND) VALUES ( 'Tesla' );
insert into brand(BRAND) VALUES ( 'BWM' );
insert into brand(BRAND) VALUES ( 'Toyota' );
insert into brand(BRAND) VALUES ( 'Xiaomi' );
insert into brand(BRAND) VALUES ( 'Volkswagen' );

insert into type(BRAND, NAME) VALUES ( 'Tesla','Model S' );
insert into type(BRAND, NAME) VALUES ( 'Tesla','Model 3' );
insert into type(BRAND, NAME) VALUES ( 'Tesla','Model X' );
insert into type(BRAND, NAME) VALUES ( 'Tesla','Model Y' );
insert into type(BRAND, NAME) VALUES ( 'Volkswagen','ID3' );
insert into type(BRAND, NAME) VALUES ( 'Xiaomi','Soccer' );

insert into category(NAME) VALUES ( 'Car' );
insert into category(NAME) VALUES ( 'Roller' );
insert into category(NAME) VALUES ( 'Bus' );

insert into ad(creator_id, category_id, type_id, title, PRODUCT_CONDITION, price, CURRENCY, state, created)
VALUES ( 2,1,1,'Tesla Model S','USED',9999999,'HUF','Active',sysdate());

insert into ad(creator_id, category_id, type_id, title, PRODUCT_CONDITION, price,CURRENCY, state, created)
VALUES ( 1,1,2,'Tesla Model 3','USED',12000000,'HUF','Active',sysdate());

insert into ad(creator_id, category_id, type_id, title,PRODUCT_CONDITION, price,CURRENCY, state, created)
VALUES ( 1,1,4,'Tesla Model Y','NEW',15000000,'HUF','Archived',sysdate());

insert into ad(creator_id, category_id, type_id, title, PRODUCT_CONDITION, price,CURRENCY, state, created)
VALUES ( 3,1,1,'Tesla Model S','NEW',13000000,'HUF','Active',sysdate());

insert into ad(creator_id, category_id, type_id, title, PRODUCT_CONDITION, price,CURRENCY, state, created)
VALUES ( 4,1,5,'Volkswagen ID3','USED',13000000,'HUF','Archived',sysdate());

insert into ad(creator_id, category_id, type_id, title,PRODUCT_CONDITION, price,CURRENCY, state, created)
VALUES ( 4,1,5,'Volkswagen ID4','NEW',13000000,'HUF','FROZEN',sysdate());

insert into rates(STATE, DESCRIPTION, CREATED) VALUES ( 'Positive','Positive desc',sysdate());
insert into rates(STATE, DESCRIPTION, CREATED) VALUES ( 'Positive','Positive desc2',sysdate());
insert into rates(STATE, DESCRIPTION, CREATED) VALUES ( 'Positive','Positive desc3',sysdate());
insert into rates(STATE, DESCRIPTION, CREATED) VALUES ( 'Negative','NEGATIVE desc',sysdate());
insert into rates(STATE, DESCRIPTION, CREATED) VALUES ( 'Negative','NEGATIVE desc2',sysdate());
insert into rates(STATE, DESCRIPTION, CREATED) VALUES ( 'Negative','NEGATIVE desc3',sysdate());

insert into users_rates(RATE_ID, RATING_USER_ID, RATED_USER_ID, AD_ID, STATE, STATUS, ACTIVATION_CODE)
        VALUES (1,1,2,1,'SELLER','CLOSED',null);
insert into users_rates(RATE_ID, RATING_USER_ID, RATED_USER_ID, AD_ID, STATE, STATUS, ACTIVATION_CODE)
        VALUES (2,2,1,1,'BUYER','CLOSED',null);
insert into users_rates(RATE_ID, RATING_USER_ID, RATED_USER_ID, AD_ID, STATE, STATUS, ACTIVATION_CODE)
        VALUES (3,1,3,4,'SELLER','CLOSED',null);
insert into users_rates(RATE_ID, RATING_USER_ID, RATED_USER_ID, AD_ID, STATE, STATUS, ACTIVATION_CODE)
        VALUES (4,3,1,4,'BUYER','CLOSED',null);
insert into users_rates(RATE_ID, RATING_USER_ID, RATED_USER_ID, AD_ID, STATE, STATUS, ACTIVATION_CODE)
        VALUES (5,1,4,5,'SELLER','CLOSED',null);
insert into users_rates(RATE_ID, RATING_USER_ID, RATED_USER_ID, AD_ID, STATE, STATUS, ACTIVATION_CODE)
        VALUES (6,4,1,5,'BUYER','CLOSED',null);

insert into basic_ad_details(AD_ID, YEAR, BATTERY_SIZE, KM, PERFORMANCE, DRIVE, SEAT_NUMBER, CHARGE_SPEED)
 VALUES ( 1,2020,100,100000,600,'AWD',5,250);
insert into basic_ad_details(AD_ID, YEAR, BATTERY_SIZE, KM, PERFORMANCE, DRIVE, SEAT_NUMBER, CHARGE_SPEED)
 VALUES ( 2,2021,60,43222,343,'RWD',5,124);
insert into basic_ad_details(AD_ID, YEAR, BATTERY_SIZE, KM, PERFORMANCE, DRIVE, SEAT_NUMBER, CHARGE_SPEED)
 VALUES ( 3,2021,34,23,454,'RWD',5,343);
insert into basic_ad_details(AD_ID, YEAR, BATTERY_SIZE, KM, PERFORMANCE, DRIVE, SEAT_NUMBER, CHARGE_SPEED)
 VALUES ( 4,2015,12,533,674,'FWD',3,111);
insert into basic_ad_details(AD_ID, YEAR, BATTERY_SIZE, KM, PERFORMANCE, DRIVE, SEAT_NUMBER, CHARGE_SPEED)
 VALUES ( 5,2021,32,23231,234,'RWD',7,53);
insert into basic_ad_details(AD_ID, YEAR, BATTERY_SIZE, KM, PERFORMANCE, DRIVE, SEAT_NUMBER, CHARGE_SPEED)
 VALUES ( 6,2021,32,23231,234,'RWD',7,53);
insert into basic_ad_details(AD_ID, YEAR, BATTERY_SIZE, KM, PERFORMANCE, DRIVE, SEAT_NUMBER, CHARGE_SPEED)
 VALUES ( 7,2021,32,23231,234,'RWD',1,53);

insert into ad_details(AD_ID, DESCRIPTION, PRODUCT_RANGE, WEIGHT, ACCELARATION, MAX_SPEED, COLOR)
 VALUES ( 1,'Desc1',400,1211,10,200,'white');
insert into ad_details(AD_ID, DESCRIPTION, PRODUCT_RANGE, WEIGHT, ACCELARATION, MAX_SPEED, COLOR)
 VALUES ( 2,'Desc2',123,2233,4,232,'black');
insert into ad_details(AD_ID, DESCRIPTION, PRODUCT_RANGE, WEIGHT, ACCELARATION, MAX_SPEED, COLOR)
 VALUES ( 3,'Desc3',321,2322,7,323,'red');
insert into ad_details(AD_ID, DESCRIPTION, PRODUCT_RANGE, WEIGHT, ACCELARATION, MAX_SPEED, COLOR)
 VALUES ( 4,'Desc4',423,4232,9,123,'blue');
insert into ad_details(AD_ID, DESCRIPTION, PRODUCT_RANGE, WEIGHT, ACCELARATION, MAX_SPEED, COLOR)
 VALUES ( 5,'Desc5',654,1233,12,311,'gold');
insert into ad_details(AD_ID, DESCRIPTION, PRODUCT_RANGE, WEIGHT, ACCELARATION, MAX_SPEED, COLOR)
 VALUES ( 6,'Desc5',654,1233,12,311,'gold');
insert into ad_details(AD_ID, DESCRIPTION, PRODUCT_RANGE, WEIGHT, ACCELARATION, MAX_SPEED, COLOR)
 VALUES (7,'Desc7',654,1233,12,311,'gold');

insert into images (PATH, UPLOADED_TIME)
values ('1220/ad1_1.jpg',sysdate());
insert into images ( PATH, UPLOADED_TIME)
values ('1220/ad1_2.jpg',sysdate());
insert into images (PATH, UPLOADED_TIME)
values ('1220/ad2_1.jpg',sysdate());
insert into images (PATH, UPLOADED_TIME)
values ('1220/ad3_1.jpg',sysdate());
insert into images (PATH, UPLOADED_TIME)
values ('1220/ad3_2.jpg',sysdate());
insert into images (PATH, UPLOADED_TIME)
values ('1220/ad3_3.jpg',sysdate());
insert into images (PATH, UPLOADED_TIME)
values ('1220/ad4_1.jpg',sysdate());
insert into images (PATH, UPLOADED_TIME)
values ('1220/ad5_1.jpg',sysdate());
insert into images (PATH, UPLOADED_TIME)
values ('1220/ad6_1.jpg',sysdate());

insert into ad_images (AD_ID,IMAGE_ID)
values (1,2);
insert into ad_images (AD_ID,IMAGE_ID)
values (1,3);
insert into ad_images (AD_ID,IMAGE_ID)
values (2,4);
insert into ad_images (AD_ID,IMAGE_ID)
values (3,5);
insert into ad_images (AD_ID,IMAGE_ID)
values (3,6);
insert into ad_images (AD_ID,IMAGE_ID)
values (3,7);
insert into ad_images (AD_ID,IMAGE_ID)
values (4,8);
insert into ad_images (AD_ID,IMAGE_ID)
values (5,9);
insert into ad_images (AD_ID,IMAGE_ID)
values (6,10);

insert into saved_ads (USER_ID, AD_ID)
values (1,3);
insert into saved_ads (USER_ID, AD_ID)
values (1,5);