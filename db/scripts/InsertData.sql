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

insert into ad(id,creator_id, category_id, type_id, title, PRODUCT_CONDITION, price, CURRENCY, state, created)
VALUES (1, 2,1,1,'Tesla Model S','USED',9999999,'HUF','Active',sysdate());

insert into ad(id,creator_id, category_id, type_id, title, PRODUCT_CONDITION, price,CURRENCY, state, created)
VALUES (2, 1,1,2,'Tesla Model 3','USED',12000000,'HUF','Active',sysdate());

insert into ad(id,creator_id, category_id, type_id, title,PRODUCT_CONDITION, price,CURRENCY, state, created)
VALUES (3, 1,1,4,'Tesla Model Y','NEW',15000000,'HUF','Archived',sysdate());

insert into ad(id,creator_id, category_id, type_id, title, PRODUCT_CONDITION, price,CURRENCY, state, created)
VALUES (4, 3,1,1,'Tesla Model S','NEW',13000000,'HUF','Active',sysdate());

insert into ad(id,creator_id, category_id, type_id, title, PRODUCT_CONDITION, price,CURRENCY, state, created)
VALUES (5, 4,1,5,'Volkswagen ID3','USED',13000000,'HUF','Archived',sysdate());

insert into ad(id,creator_id, category_id, type_id, title,PRODUCT_CONDITION, price,CURRENCY, state, created)
VALUES (6, 4,1,5,'Volkswagen ID4','NEW',13000000,'HUF','FROZEN',sysdate());

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