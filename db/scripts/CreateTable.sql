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