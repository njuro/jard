alter table boards
    add created_at timestamp default NOW() not null;