CREATE SCHEMA IF NOT EXISTS ec2_web_stockdata;
-- --------------
-- 1) ec2_web_stockdata
-- DATE_AXIS_DD
-- --------------
create table IF NOT EXISTS date_axis_dd
(
    yyyy     text     null,
    mm       text     null,
    dd       text     null,
    yyyymmdd datetime not null
        primary key
);

create or replace index date_axis_dd_yyyymmdd_index
    on date_axis_dd (yyyymmdd);

-- --------------
-- 2) ec2_web_stockdata
-- exchange_rate_dollar_day
-- --------------
create table IF NOT EXISTS exchange_rate_dollar_day
(
    STAT_NAME  text     null,
    STAT_CODE  text     null,
    ITEM_CODE1 text     null,
    ITEM_CODE2 text     null,
    ITEM_CODE3 text     null,
    ITEM_NAME1 text     null,
    ITEM_NAME2 text     null,
    ITEM_NAME3 text     null,
    DATA_VALUE double   null,
    TIME       datetime not null
        primary key
);

create or replace index exchange_rate_dollar_day_TIME_index
    on exchange_rate_dollar_day (TIME);

-- --------------
-- 3) ec2_web_stockdata
-- kospi_day
-- --------------
create table IF NOT EXISTS kospi_day
(
    STAT_NAME  text     null,
    STAT_CODE  text     null,
    ITEM_CODE1 text     null,
    ITEM_CODE2 text     null,
    ITEM_CODE3 text     null,
    ITEM_NAME1 text     null,
    ITEM_NAME2 text     null,
    ITEM_NAME3 text     null,
    DATA_VALUE text     null,
    TIME       datetime null,
    constraint kospi_day_pk
        unique (TIME)
);

create or replace index kospi_day_TIME_index
    on kospi_day (TIME);

-- --------------
-- 4) ec2_web_stockdata
-- loan_corporate_month
-- --------------
create table IF NOT EXISTS loan_corporate_month
(
    UNIT_NAME  text     null,
    STAT_NAME  text     null,
    STAT_CODE  text     null,
    ITEM_CODE1 text     null,
    ITEM_CODE2 text     null,
    ITEM_CODE3 text     null,
    ITEM_NAME1 text     null,
    ITEM_NAME2 text     null,
    ITEM_NAME3 text     null,
    DATA_VALUE double   null,
    TIME       datetime null,
    constraint loan_corporate_month_pk
        unique (TIME)
);

create or replace index loan_corporate_month_TIME_index
    on loan_corporate_month (TIME);

-- --------------
-- 5) ec2_web_stockdata
-- loan_household_month
-- --------------
create table IF NOT EXISTS loan_household_month
(
    UNIT_NAME  text     null,
    STAT_NAME  text     null,
    STAT_CODE  text     null,
    ITEM_CODE1 text     null,
    ITEM_CODE2 text     null,
    ITEM_CODE3 text     null,
    ITEM_NAME1 text     null,
    ITEM_NAME2 text     null,
    ITEM_NAME3 text     null,
    DATA_VALUE double   null,
    TIME       datetime not null
        primary key
);

create or replace index loan_household_month_TIME_index
    on loan_household_month (TIME);

-- --------------
-- 6) ec2_web_stockdata
-- loan_rate_kor
-- --------------
create table IF NOT EXISTS loan_rate_kor
(
    UNIT_NAME  text     null,
    STAT_NAME  text     null,
    STAT_CODE  text     null,
    ITEM_CODE1 text     null,
    ITEM_CODE2 text     null,
    ITEM_CODE3 text     null,
    ITEM_NAME1 text     null,
    ITEM_NAME2 text     null,
    ITEM_NAME3 text     null,
    DATA_VALUE double   null,
    TIME       datetime null,
    constraint loan_rate_kor_pk
        unique (TIME)
);

create or replace index loan_rate_kor_TIME_index
    on loan_rate_kor (TIME);

-- --------------
-- 7) ec2_web_stockdata
-- loan_rate_usa
-- --------------
create table IF NOT EXISTS loan_rate_usa
(
    UNIT_NAME  text     null,
    STAT_NAME  text     null,
    STAT_CODE  text     null,
    ITEM_CODE1 text     null,
    ITEM_CODE2 text     null,
    ITEM_CODE3 text     null,
    ITEM_NAME1 text     null,
    ITEM_NAME2 text     null,
    ITEM_NAME3 text     null,
    DATA_VALUE double   null,
    TIME       datetime not null
        primary key
);

create or replace index loan_rate_usa_TIME_index
    on loan_rate_usa (TIME);


--
-- 데이터 제거
-- 1) DATE_AXIS_DD
DELETE FROM date_axis_dd WHERE 1=1;

-- 2) exchange_rate_dollar_day
DELETE FROM exchange_rate_dollar_day WHERE 1=1;

-- 3) kospi_day
DELETE FROM kospi_day WHERE 1=1;

-- 4) loan_corporate_month
DELETE FROM loan_corporate_month WHERE 1=1;

-- 5) loan_household_month
DELETE FROM loan_household_month WHERE 1=1;

-- 6) loan_rate_kor
DELETE FROM loan_rate_kor WHERE 1=1;

-- 7) loan_rate_usa
DELETE FROM loan_rate_usa WHERE 1=1;

commit;