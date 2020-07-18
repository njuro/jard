create table attachments_embed_data
(
    attachment_id uuid not null,
    category      varchar(255) not null,
    embed_url     varchar(255) not null,
    thumbnail_url varchar(255),
    provider_name varchar(255) not null,
    uploader_name varchar(255) not null,
    rendered_html TEXT         not null,
    primary key (attachment_id)
);

alter table if exists attachments_embed_data
    add constraint FK_attachments_embed_data
    foreign key (attachment_id)
    references attachments;

alter table attachments
    alter column filename drop not null;