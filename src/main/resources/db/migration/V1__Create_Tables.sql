create table attachments
(
    id                    uuid         not null,
    amazons3thumbnail_url varchar(255),
    amazons3url           varchar(255),
    category              varchar(255),
    filename              varchar(255) not null,
    folder                varchar(255),
    original_filename     varchar(255) not null,
    thumbnail_filename    varchar(255),
    primary key (id)
);

create table attachments_metadata
(
    attachment_id    uuid         not null,
    checksum         varchar(255) not null,
    duration         varchar(255),
    file_size        varchar(255) not null,
    height           int4         not null,
    mime_type        varchar(255) not null,
    thumbnail_height int4         not null,
    thumbnail_width  int4         not null,
    width            int4         not null,
    primary key (attachment_id)
);

create table bans
(
    id             uuid         not null,
    ip             varchar(255) not null,
    reason         varchar(255),
    status         varchar(255) not null,
    unban_reason   varchar(255) not null,
    valid_from     timestamp    not null,
    valid_to       timestamp,
    banned_by_id   uuid,
    unbanned_by_id uuid,
    primary key (id)
);

create table board_attachment_categories
(
    board_id            uuid not null,
    attachment_category varchar(255)
);

create table board_settings
(
    board_id                  uuid             not null,
    bump_limit                int4 default 300 not null,
    default_poster_name       varchar(255),
    force_default_poster_name boolean          not null,
    nsfw                      boolean          not null,
    thread_limit              int4 default 100 not null,
    primary key (board_id)
);

create table boards
(
    id           uuid         not null,
    label        varchar(255) not null,
    name         varchar(255),
    post_counter int8         not null,
    primary key (id)
);

create table posts
(
    id            uuid         not null,
    body          TEXT,
    created_at    timestamp    not null,
    ip            varchar(255) not null,
    name          varchar(255),
    post_number   int8         not null,
    sage          boolean      not null,
    tripcode      varchar(255),
    attachment_id uuid,
    thread_id     uuid,
    primary key (id)
);

create table threads
(
    id               uuid      not null,
    created_at       timestamp not null,
    last_bump_at     timestamp not null,
    last_reply_at    timestamp not null,
    locked           boolean   not null,
    stickied         boolean   not null,
    subject          varchar(255),
    board_id         uuid      not null,
    original_post_id uuid,
    primary key (id)
);

create table user_authorities
(
    user_id   uuid not null,
    authority varchar(255)
);

create table users
(
    id              uuid         not null,
    created_at      timestamp    not null,
    email           varchar(255),
    enabled         boolean      not null,
    last_login      timestamp,
    last_login_ip   varchar(255),
    password        varchar(255) not null,
    registration_ip varchar(255),
    role            varchar(255) not null,
    username        varchar(255) not null,
    primary key (id)
);

alter table if exists attachments
    add constraint UK_124krfsd4bd49ul6vwxgsdmoe unique (filename);
alter table if exists attachments
    add constraint UK_badqm7ie8fmvvjpce7i7894vs unique (thumbnail_filename);

alter table if exists boards
    add constraint UK_innomyl739at6gnj9bidg046l unique (label);

alter table if exists users
    add constraint UK_6dotkott2kjsp8vw4d0m25fb7 unique (email);
alter table if exists users
    add constraint UK_r43af9ap4edm43mmtq01oddj6 unique (username);

alter table if exists attachments_metadata
    add constraint FK4vgnhip661am34bd4sbkfusvx
        foreign key (attachment_id)
            references attachments;

alter table if exists bans
    add constraint FKmogn1sdhbvpudbu8kt5atgpoa
        foreign key (banned_by_id)
            references users;
alter table if exists bans
    add constraint FKrm422t20seaijvssjwmiglnbm
        foreign key (unbanned_by_id)
            references users;

alter table if exists board_attachment_categories
    add constraint FKh9vg2xrc1f7sups5lg501cclv
        foreign key (board_id)
            references board_settings;

alter table if exists board_settings
    add constraint FKpg2qhgav778aec1c0wxca4q4j
        foreign key (board_id)
            references boards;

alter table if exists posts
    add constraint FK6c6j7r8hea4phpaitrdmm89ge
        foreign key (attachment_id)
            references attachments;
alter table if exists posts
    add constraint FK2h178flnfq5ha27wy8of7p6xs
        foreign key (thread_id)
            references threads;

alter table if exists threads
    add constraint FKqten2y0tucolwx5cvh8l0qdlp
        foreign key (board_id)
            references boards;
alter table if exists threads
    add constraint FK8r5xhi9ka8xaqopha8o622hf1
        foreign key (original_post_id)
            references posts;

alter table if exists user_authorities
    add constraint FKhiiib540jf74gksgb87oofni
        foreign key (user_id)
            references users;

