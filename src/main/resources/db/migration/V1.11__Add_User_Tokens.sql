create table user_tokens
(
    value         varchar(255) not null,
    user_id       uuid,
    type          varchar(255),
    issued_at     timestamp    not null,
    expiration_at timestamp    not null,
    primary key (value)
);

alter table if exists user_tokens
    add constraint FK_user_tokens_users
        foreign key (user_id)
            references users;
