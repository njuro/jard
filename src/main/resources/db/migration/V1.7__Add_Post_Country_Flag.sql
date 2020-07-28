alter table posts
    add country_code varchar(255);

alter table posts
    add country_name varchar(255);

alter table board_settings
    add country_flags boolean not null default false;