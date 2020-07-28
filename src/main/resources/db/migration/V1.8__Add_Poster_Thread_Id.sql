alter table posts
    add poster_thread_id varchar(255);

alter table board_settings
    add poster_thread_ids boolean not null default false;