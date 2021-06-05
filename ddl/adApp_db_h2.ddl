
DROP TABLE AD CASCADE CONSTRAINTS;
DROP TABLE AD_IMAGES CASCADE CONSTRAINTS;
DROP TABLE IMAGES CASCADE CONSTRAINTS;
DROP TABLE BRAND CASCADE CONSTRAINTS;
DROP TABLE TYPE CASCADE CONSTRAINTS;
DROP TABLE CATEGORY CASCADE CONSTRAINTS;
DROP TABLE USERS_RATES CASCADE CONSTRAINTS;
DROP TABLE USERS_MESSAGES CASCADE CONSTRAINTS;
DROP TABLE MESSAGES CASCADE CONSTRAINTS;
DROP TABLE AD_DETAILS CASCADE CONSTRAINTS;
DROP TABLE BASIC_AD_DETAILS CASCADE CONSTRAINTS;
DROP TABLE RATES CASCADE CONSTRAINTS;
DROP TABLE USERS_ROLES CASCADE CONSTRAINTS;
DROP TABLE ROLES CASCADE CONSTRAINTS;
DROP TABLE USERDATA CASCADE CONSTRAINTS;
DROP TABLE USERS CASCADE CONSTRAINTS;
DROP TABLE SAVED_ADS CASCADE CONSTRAINTS;

CREATE TABLE IMAGES(
    ID NUMBER GENERATED ALWAYS as IDENTITY(START with 1 INCREMENT by 1),
    PATH VARCHAR2 NOT NULL,
    UPLOADED_TIME TIMESTAMP NOT NULL,
    constraint i_pk primary key (ID),
    constraint i_path_uq unique (PATH)
);

CREATE TABLE USERS (
    ID NUMBER GENERATED ALWAYS as IDENTITY(START with 1 INCREMENT by 1),
    USERNAME VARCHAR2 NOT NULL,
    PASSWORD VARCHAR2 NOT NULL,
    EMAIL VARCHAR2 NOT NULL,
    ENABLED BOOLEAN,
    PROFILE_IMG_ID NUMBER,
    ACTIVATION VARCHAR2,
    LAST_LOGIN TIMESTAMP,
    CREATED TIMESTAMP NOT NULL,
    constraint u_pk primary key (ID),
    constraint u_username_uq unique (USERNAME),
    constraint u_email_uq unique (EMAIL),
    constraint u_fk_i foreign key (PROFILE_IMG_ID) references IMAGES(ID)
);

CREATE TABLE USERDATA(
    USER_ID NUMBER,
    CITY VARCHAR2,
    FULL_NAME VARCHAR2,
    PUBLIC_EMAIL VARCHAR2,
    PHONE_NUMBER VARCHAR2,
    constraint ud_fk_u foreign key (USER_ID) references USERS(ID),
    constraint ud_pk primary key (USER_ID)
);

CREATE TABLE ROLES (
    ID NUMBER GENERATED ALWAYS as IDENTITY(START with 1 INCREMENT by 1),
    ROLE_NAME VARCHAR2(20) NOT NULL,
    constraint role_pk primary key (ID),
    constraint role_name_uq unique (ROLE_NAME)
);

CREATE TABLE USERS_ROLES(
    USER_ID NUMBER,
    ROLE_ID NUMBER,
    constraint ur_fk_u foreign key (USER_ID) references USERS(ID),
    constraint ur_fk_r foreign key (ROLE_ID) references ROLES(ID),
    constraint ur_pk primary key (USER_ID,ROLE_ID)
);

CREATE TABLE MESSAGES(
    ID NUMBER GENERATED ALWAYS as IDENTITY(START with 1 INCREMENT by 1),
    CONTENT VARCHAR2(1000) NOT NULL,
    constraint m_pk primary key (ID)
);
CREATE TABLE USERS_MESSAGES(
    MESSAGE_ID NUMBER,
    SENDER_ID NUMBER,
    RECEIVER_ID NUMBER,
    SENT_TIME TIMESTAMP,
    UNREAD BOOLEAN,
    constraint um_fk_m foreign key (MESSAGE_ID) references MESSAGES(ID),
    constraint um_fk_s foreign key (SENDER_ID) references USERS(ID),
    constraint um_fk_r foreign key (RECEIVER_ID) references USERS(ID),
    constraint um_pk primary key (MESSAGE_ID,SENDER_ID,RECEIVER_ID)
);

CREATE TABLE CATEGORY(
    ID NUMBER GENERATED ALWAYS as IDENTITY(START with 1 INCREMENT by 1),
    NAME VARCHAR2(30) NOT NULL,
    constraint c_pk primary key (ID),
    constraint c_name unique (NAME)
);
CREATE TABLE BRAND(
    BRAND VARCHAR2(50),
    constraint b_pk primary key (BRAND)
);
CREATE TABLE TYPE(
    ID NUMBER GENERATED ALWAYS as IDENTITY(START with 1 INCREMENT by 1),
    BRAND VARCHAR2(50),
    NAME VARCHAR2(50),
    constraint t_fk_b foreign key (BRAND) references BRAND(BRAND),
    constraint t_pk primary key (ID)
);

CREATE TABLE AD(
    ID NUMBER GENERATED ALWAYS as IDENTITY(START with 1 INCREMENT by 1),
    CREATOR_ID NUMBER,
    CATEGORY_ID NUMBER,
    TYPE_ID NUMBER,
    TITLE VARCHAR2(50) NOT NULL,
    PRICE NUMBER(10) NOT NULL,
    CONDITION ENUM('USED','NEW','SPARED'),
    STATE ENUM('ACTIVE','ARCHIVED','FROZEN') NOT NULL,
    CREATED TIMESTAMP NOT NULL,
    constraint a_pk primary key (ID),
    constraint a_fk_u foreign key (CREATOR_ID) references USERS(ID),
    constraint a_fk_c foreign key (CATEGORY_ID) references CATEGORY(ID),
    constraint a_fk_t foreign key (TYPE_ID) references TYPE(ID)
);
CREATE TABLE AD_IMAGES(
    AD_ID NUMBER,
    IMAGE_ID NUMBER,
    constraint ai_fk_ad foreign key (AD_ID) references AD(ID),
    constraint ai_fk_im foreign key (IMAGE_ID) references IMAGES(ID),
    constraint aim_pk primary key (AD_ID,IMAGE_ID)
);

CREATE TABLE BASIC_AD_DETAILS(
    AD_ID NUMBER,
    YEAR NUMBER(4),
    BATTERY_SIZE NUMBER(5),
    KM NUMBER(7),
    PERFORMANCE NUMBER(5),
    DRIVE ENUM('AWD','RWD','FWD'),
    SEAT_NUMBER NUMBER(3),
    CHARGE_SPEED NUMBER(6),
    constraint bd_fk_ad foreign key (AD_ID) references AD(ID),
    constraint bd_pk primary key (AD_ID)
);

CREATE TABLE AD_DETAILS(
    AD_ID NUMBER,
    DESCRIPTION VARCHAR2(1500),
    RANGE NUMBER(5),
    WEIGHT NUMBER(6),
    ACCELARATION NUMBER(5,2),
    MAX_SPEED NUMBER(4),
    COLOR VARCHAR2(25),
    constraint pd_fk_ad foreign key (AD_ID) references AD(ID),
    constraint pd_pk primary key (AD_ID)
);

CREATE TABLE SAVED_ADS(
    USER_ID NUMBER,
    AD_ID NUMBER,
    constraint sa_fk_u foreign key (USER_ID) references USERS(ID),
    constraint sa_fk_a foreign key (AD_ID) references AD(ID),
    constraint sa_pk primary key (USER_ID,AD_ID)
);

CREATE TABLE RATES(
    ID NUMBER GENERATED ALWAYS as IDENTITY(START with 1 INCREMENT by 1),
    STATE ENUM('POSITIVE','NEGATIVE'),
    DESCRIPTION VARCHAR2(500),
    CREATED TIMESTAMP,
    constraint r_pk primary key (ID)
);

CREATE TABLE USERS_RATES(
    RATE_ID NUMBER,
    RATING_USER_ID NUMBER,
    RATED_USER_ID NUMBER,
    AD_ID NUMBER,
    STATE ENUM('SELLER','BUYER'),
    STATUS ENUM('OPEN','CLOSED'),
    ACTIVATION_CODE VARCHAR2,
    constraint urates_id_pk primary key (RATE_ID),
    constraint urates_fk_r foreign key (RATE_ID) references RATES(ID),
    constraint urates_rating_u_fk foreign key (RATING_USER_ID) references USERS(ID),
    constraint urates_rated_u_fk foreign key (RATED_USER_ID) references USERS(ID),
    constraint urates_fk_a foreign key (AD_ID) references AD(ID)
);

insert into IMAGES (PATH, UPLOADED_TIME)
values ('images/profiles/default.jpg',sysdate());

insert into USERS(USERNAME, PASSWORD, EMAIL, ENABLED, ACTIVATION, LAST_LOGIN, CREATED,PROFILE_IMG_ID)
    VALUES ('user1','pass1','email1',true,null,sysdate(),sysdate(),1);
insert into USERS(USERNAME, PASSWORD, EMAIL, ENABLED, ACTIVATION, LAST_LOGIN, CREATED,PROFILE_IMG_ID)
    VALUES ('user2','pass2','email2',true,null,sysdate(),sysdate(),1);
insert into USERS(USERNAME, PASSWORD, EMAIL, ENABLED, ACTIVATION, LAST_LOGIN, CREATED,PROFILE_IMG_ID)
    VALUES ('user3','pass3','email3',true,null,sysdate(),sysdate(),1);
insert into USERS(USERNAME, PASSWORD, EMAIL, ENABLED, ACTIVATION, LAST_LOGIN, CREATED,PROFILE_IMG_ID)
    VALUES ('user4','pass4','email4',true,null,sysdate(),sysdate(),1);
insert into USERS(USERNAME, PASSWORD, EMAIL, ENABLED, ACTIVATION, LAST_LOGIN, CREATED,PROFILE_IMG_ID)
    VALUES ('user5','pass5','email5',true,null,sysdate(),sysdate(),1);

insert into USERDATA(USER_ID, CITY, FULL_NAME, PUBLIC_EMAIL, PHONE_NUMBER)
    VALUES ( 1,'city1','fullname1','pemail1','705551286' );
insert into USERDATA(USER_ID, CITY, FULL_NAME, PUBLIC_EMAIL, PHONE_NUMBER)
    VALUES ( 2,'city2','fullname2','pemail2','705551286' );
insert into USERDATA(USER_ID, CITY, FULL_NAME, PUBLIC_EMAIL, PHONE_NUMBER)
    VALUES ( 3,'city3','fullname3','pemail3','705551286' );
insert into USERDATA(USER_ID, CITY, FULL_NAME, PUBLIC_EMAIL, PHONE_NUMBER)
    VALUES ( 4,null,null,null,'705551286' );
insert into USERDATA(USER_ID, CITY, FULL_NAME, PUBLIC_EMAIL, PHONE_NUMBER)
    VALUES (5,null,'fullname5',null,'705551286' );

insert into ROLES(ROLE_NAME) values ('USER');
insert into ROLES(ROLE_NAME) values ('ADMIN');

insert into USERS_ROLES(USER_ID, ROLE_ID) VALUES ( 1,2 );
insert into USERS_ROLES(USER_ID, ROLE_ID) VALUES ( 2,1 );
insert into USERS_ROLES(USER_ID, ROLE_ID) VALUES ( 3,1 );
insert into USERS_ROLES(USER_ID, ROLE_ID) VALUES ( 4,1 );
insert into USERS_ROLES(USER_ID, ROLE_ID) VALUES ( 5,1 );

insert into MESSAGES(CONTENT) VALUES ( 'Message1 content');
insert into MESSAGES(CONTENT) VALUES ( 'Message2 content');
insert into MESSAGES(CONTENT) VALUES ( 'Message3 content');
insert into MESSAGES(CONTENT) VALUES ( 'Message4 content');
insert into MESSAGES(CONTENT) VALUES ( 'Message5 content');
insert into MESSAGES(CONTENT) VALUES ( 'Message6 content');
insert into MESSAGES(CONTENT) VALUES ( 'Message7 content');

insert into USERS_MESSAGES(MESSAGE_ID, SENDER_ID, RECEIVER_ID, SENT_TIME, UNREAD)
    VALUES ( 1,1,2,sysdate(),false);

insert into USERS_MESSAGES(MESSAGE_ID, SENDER_ID, RECEIVER_ID, SENT_TIME, UNREAD)
    VALUES ( 2,2,3,sysdate(),false);

insert into USERS_MESSAGES(MESSAGE_ID, SENDER_ID, RECEIVER_ID, SENT_TIME, UNREAD)
    VALUES ( 3,3,2,sysdate(),false);

insert into USERS_MESSAGES(MESSAGE_ID, SENDER_ID, RECEIVER_ID, SENT_TIME, UNREAD)
    VALUES ( 4,4,5,sysdate(),false);

insert into USERS_MESSAGES(MESSAGE_ID, SENDER_ID, RECEIVER_ID, SENT_TIME, UNREAD)
    VALUES ( 5,5,4,sysdate(),false);

insert into USERS_MESSAGES(MESSAGE_ID, SENDER_ID, RECEIVER_ID, SENT_TIME, UNREAD)
    VALUES ( 6,3,1,sysdate(),false);

insert into USERS_MESSAGES(MESSAGE_ID, SENDER_ID, RECEIVER_ID, SENT_TIME, UNREAD)
    VALUES ( 7,1,5,sysdate(),true);

insert into BRAND(BRAND) VALUES ( 'Tesla' );
insert into BRAND(BRAND) VALUES ( 'BWM' );
insert into BRAND(BRAND) VALUES ( 'Toyota' );
insert into BRAND(BRAND) VALUES ( 'Xiaomi' );
insert into BRAND(BRAND) VALUES ( 'Volkswagen' );

insert into TYPE(BRAND, NAME) VALUES ( 'Tesla','Model S' );
insert into TYPE(BRAND, NAME) VALUES ( 'Tesla','Model 3' );
insert into TYPE(BRAND, NAME) VALUES ( 'Tesla','Model X' );
insert into TYPE(BRAND, NAME) VALUES ( 'Tesla','Model Y' );
insert into TYPE(BRAND, NAME) VALUES ( 'Volkswagen','ID3' );
insert into TYPE(BRAND, NAME) VALUES ( 'Xiaomi','Soccer' );

insert into CATEGORY(NAME) VALUES ( 'Car' );
insert into CATEGORY(NAME) VALUES ( 'Roller' );
insert into CATEGORY(NAME) VALUES ( 'Bus' );

insert into AD(creator_id, category_id, type_id, title, CONDITION, price, state, created)
VALUES ( 2,1,1,'Tesla Model S','USED',9999999,'Active',sysdate());

insert into AD(creator_id, category_id, type_id, title, CONDITION, price, state, created)
VALUES ( 1,1,2,'Tesla Model 3','USED',12000000,'Active',sysdate());

insert into AD(creator_id, category_id, type_id, title, CONDITION, price, state, created)
VALUES ( 1,1,4,'Tesla Model Y','NEW',15000000,'Archived',sysdate());

insert into AD(creator_id, category_id, type_id, title, CONDITION, price, state, created)
VALUES ( 3,1,1,'Tesla Model S','NEW',13000000,'Active',sysdate());

insert into AD(creator_id, category_id, type_id, title, CONDITION, price, state, created)
VALUES ( 4,1,5,'Volkswagen ID3','USED',13000000,'Archived',sysdate());

insert into AD(creator_id, category_id, type_id, title, CONDITION, price, state, created)
VALUES ( 4,1,5,'Volkswagen ID4','NEW',13000000,'FROZEN',sysdate());

insert into AD(creator_id, category_id, type_id, title, CONDITION, price, state, created)
VALUES ( 4,2,5,'Xiaomi roller','NEW',12222,'ACTIVE',sysdate());

insert into RATES(STATE, DESCRIPTION, CREATED) VALUES ( 'Positive','Positive desc',sysdate());
insert into RATES(STATE, DESCRIPTION, CREATED) VALUES ( 'Positive','Positive desc2',sysdate());
insert into RATES(STATE, DESCRIPTION, CREATED) VALUES ( 'Positive','Positive desc3',sysdate());
insert into RATES(STATE, DESCRIPTION, CREATED) VALUES ( 'Negative','NEGATIVE desc',sysdate());
insert into RATES(STATE, DESCRIPTION, CREATED) VALUES ( 'Negative','NEGATIVE desc2',sysdate());
insert into RATES(STATE, DESCRIPTION, CREATED) VALUES ( 'Negative','NEGATIVE desc3',sysdate());

insert into USERS_RATES(RATE_ID, RATING_USER_ID, RATED_USER_ID, AD_ID, STATE, STATUS, ACTIVATION_CODE)
        VALUES (1,1,2,1,'SELLER','CLOSED',null);
insert into USERS_RATES(RATE_ID, RATING_USER_ID, RATED_USER_ID, AD_ID, STATE, STATUS, ACTIVATION_CODE)
        VALUES (2,2,1,1,'BUYER','CLOSED',null);
insert into USERS_RATES(RATE_ID, RATING_USER_ID, RATED_USER_ID, AD_ID, STATE, STATUS, ACTIVATION_CODE)
        VALUES (3,1,3,4,'SELLER','CLOSED',null);
insert into USERS_RATES(RATE_ID, RATING_USER_ID, RATED_USER_ID, AD_ID, STATE, STATUS, ACTIVATION_CODE)
        VALUES (4,3,1,4,'BUYER','CLOSED',null);
insert into USERS_RATES(RATE_ID, RATING_USER_ID, RATED_USER_ID, AD_ID, STATE, STATUS, ACTIVATION_CODE)
        VALUES (5,1,4,5,'SELLER','CLOSED',null);
insert into USERS_RATES(RATE_ID, RATING_USER_ID, RATED_USER_ID, AD_ID, STATE, STATUS, ACTIVATION_CODE)
        VALUES (6,4,1,5,'BUYER','CLOSED',null);

insert into BASIC_AD_DETAILS(AD_ID, YEAR, BATTERY_SIZE, KM, PERFORMANCE, DRIVE, SEAT_NUMBER, CHARGE_SPEED)
 VALUES ( 1,2020,100,100000,600,'AWD',5,250);
insert into BASIC_AD_DETAILS(AD_ID, YEAR, BATTERY_SIZE, KM, PERFORMANCE, DRIVE, SEAT_NUMBER, CHARGE_SPEED)
 VALUES ( 2,2021,60,43222,343,'RWD',5,124);
insert into BASIC_AD_DETAILS(AD_ID, YEAR, BATTERY_SIZE, KM, PERFORMANCE, DRIVE, SEAT_NUMBER, CHARGE_SPEED)
 VALUES ( 3,2021,34,23,454,'RWD',5,343);
insert into BASIC_AD_DETAILS(AD_ID, YEAR, BATTERY_SIZE, KM, PERFORMANCE, DRIVE, SEAT_NUMBER, CHARGE_SPEED)
 VALUES ( 4,2015,12,533,674,'FWD',3,111);
insert into BASIC_AD_DETAILS(AD_ID, YEAR, BATTERY_SIZE, KM, PERFORMANCE, DRIVE, SEAT_NUMBER, CHARGE_SPEED)
 VALUES ( 5,2021,32,23231,234,'RWD',7,53);
insert into BASIC_AD_DETAILS(AD_ID, YEAR, BATTERY_SIZE, KM, PERFORMANCE, DRIVE, SEAT_NUMBER, CHARGE_SPEED)
 VALUES ( 6,2021,32,23231,234,'RWD',7,53);
insert into BASIC_AD_DETAILS(AD_ID, YEAR, BATTERY_SIZE, KM, PERFORMANCE, DRIVE, SEAT_NUMBER, CHARGE_SPEED)
 VALUES ( 7,2021,32,23231,234,'RWD',1,53);

insert into AD_DETAILS(AD_ID, DESCRIPTION, RANGE, WEIGHT, ACCELARATION, MAX_SPEED, COLOR)
 VALUES ( 1,'Desc1',400,1211,10,200,'white');
insert into AD_DETAILS(AD_ID, DESCRIPTION, RANGE, WEIGHT, ACCELARATION, MAX_SPEED, COLOR)
 VALUES ( 2,'Desc2',123,2233,4,232,'black');
insert into AD_DETAILS(AD_ID, DESCRIPTION, RANGE, WEIGHT, ACCELARATION, MAX_SPEED, COLOR)
 VALUES ( 3,'Desc3',321,2322,7,323,'red');
insert into AD_DETAILS(AD_ID, DESCRIPTION, RANGE, WEIGHT, ACCELARATION, MAX_SPEED, COLOR)
 VALUES ( 4,'Desc4',423,4232,9,123,'blue');
insert into AD_DETAILS(AD_ID, DESCRIPTION, RANGE, WEIGHT, ACCELARATION, MAX_SPEED, COLOR)
 VALUES ( 5,'Desc5',654,1233,12,311,'gold');
insert into AD_DETAILS(AD_ID, DESCRIPTION, RANGE, WEIGHT, ACCELARATION, MAX_SPEED, COLOR)
 VALUES ( 6,'Desc5',654,1233,12,311,'gold');
insert into AD_DETAILS(AD_ID, DESCRIPTION, RANGE, WEIGHT, ACCELARATION, MAX_SPEED, COLOR)
 VALUES (7,'Desc7',654,1233,12,311,'gold');

-- insert into IMAGES (PATH, UPLOADED_TIME)
-- values ('/1220/ad1_1.jpg',sysdate());
-- insert into IMAGES ( PATH, UPLOADED_TIME)
-- values ('/1220/ad1_2.jpg',sysdate());
-- insert into IMAGES (PATH, UPLOADED_TIME)
-- values ('/1220/ad2_1.jpg',sysdate());
-- insert into IMAGES (PATH, UPLOADED_TIME)
-- values ('/1220/ad3_1.jpg',sysdate());
-- insert into IMAGES (PATH, UPLOADED_TIME)
-- values ('/1220/ad3_2.jpg',sysdate());
-- insert into IMAGES (PATH, UPLOADED_TIME)
-- values ('/1220/ad3_3.jpg',sysdate());
-- insert into IMAGES (PATH, UPLOADED_TIME)
-- values ('/1220/ad4_1.jpg',sysdate());
-- insert into IMAGES (PATH, UPLOADED_TIME)
-- values ('/1220/ad5_1.jpg',sysdate());
-- insert into IMAGES (PATH, UPLOADED_TIME)
-- values ('/1220/ad6_1.jpg',sysdate());
--
-- insert into AD_IMAGES (AD_ID,IMAGE_ID)
-- values (1,2);
-- insert into AD_IMAGES (AD_ID,IMAGE_ID)
-- values (1,3);
-- insert into AD_IMAGES (AD_ID,IMAGE_ID)
-- values (2,4);
-- insert into AD_IMAGES (AD_ID,IMAGE_ID)
-- values (3,5);
-- insert into AD_IMAGES (AD_ID,IMAGE_ID)
-- values (3,6);
-- insert into AD_IMAGES (AD_ID,IMAGE_ID)
-- values (3,7);
-- insert into AD_IMAGES (AD_ID,IMAGE_ID)
-- values (4,8);
-- insert into AD_IMAGES (AD_ID,IMAGE_ID)
-- values (5,9);
-- insert into AD_IMAGES (AD_ID,IMAGE_ID)
-- values (6,10);

insert into SAVED_ADS (USER_ID, AD_ID)
values (1,3);
insert into SAVED_ADS (USER_ID, AD_ID)
values (1,5);