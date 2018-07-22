SET FOREIGN_KEY_CHECKS = 0;
INSERT INTO attachments (id, filename, height, original_filename, path, thumb_height, thumb_width, width)
VALUES (1, '1532253934143.jpg', 475, '30659.jpg', 'r', 301, 197, 309);
INSERT INTO attachments (id, filename, height, original_filename, path, thumb_height, thumb_width, width)
VALUES (2, '1532254308096.jpg', 575, 'boss_dubcek.jpg', 'r', 301, 228, 434);
INSERT INTO attachments (id, filename, height, original_filename, path, thumb_height, thumb_width, width)
VALUES (3, '1532254438225.jpg', 1200, 'brawl.jpg', 'r', 301, 159, 630);
INSERT INTO attachments (id, filename, height, original_filename, path, thumb_height, thumb_width, width)
VALUES (4, '1532254533363.jpg', 708, 'Interview-with-Scott-Herman.jpg', 'fit', 301, 213, 500);
INSERT INTO boards (id, label, name, post_counter, type) VALUES (1, 'r', 'Random', 5, 'TEXT');
INSERT INTO boards (id, label, name, post_counter, type) VALUES (2, 'fit', 'Fitness', 2, 'TEXT');
INSERT INTO boards (id, label, name, post_counter, type) VALUES (3, 'sp', 'Sports', 1, 'IMAGE');
INSERT INTO posts (id, body, created_at, name, post_number, tripcode, attachment_id, thread_id)
VALUES (1, 'This thread is stickied & locked.', '2018-07-22 12:05:34', 'Admin', 1, '!ab0cacbc86', 1, 1);
INSERT INTO posts (id, body, created_at, name, post_number, tripcode, attachment_id, thread_id)
VALUES (2, 'This is just a normal thread.', '2018-07-22 12:11:48', 'Anonymous', 2, '!7af927da3e', 2, 2);
INSERT INTO posts (id, body, created_at, name, post_number, tripcode, attachment_id, thread_id)
VALUES (3, 'And normal reply without image.', '2018-07-22 12:12:04', 'Anonymous', 3, '!7af927da3e', null, 2);
INSERT INTO posts (id, body, created_at, name, post_number, tripcode, attachment_id, thread_id) VALUES (4, 'Some reply referencing first

OP
>>2

Then previous post
>>3

And finally sticky >>1

>also
>greentext', '2018-07-22 12:13:58', 'Anonymous', 4, '!7af927da3e', 3, 2);
INSERT INTO posts (id, body, created_at, name, post_number, tripcode, attachment_id, thread_id) VALUES (5, 'First reference cross board thread >>>/r/1
And crossboard post >>>/r/2
Invalid post >>2
Invalid crossboard post >>>/a/1', '2018-07-22 12:15:33', 'John', 1, '!7af927da3e', 4, 3);
INSERT INTO threads (id, created_at, locked, stickied, subject, board_id, original_post_id)
VALUES (1, '2018-07-22 12:05:34', true, true, 'Stickied thread', 1, 1);
INSERT INTO threads (id, created_at, locked, stickied, subject, board_id, original_post_id)
VALUES (2, '2018-07-22 12:11:48', false, false, '', 1, 2);
INSERT INTO threads (id, created_at, locked, stickied, subject, board_id, original_post_id)
VALUES (3, '2018-07-22 12:15:33', false, false, 'Backlink thread', 2, 5);
INSERT INTO users (id, created_at, email, enabled, last_login, last_login_ip, password, registration_ip, role, username)
VALUES (1, '2018-07-22 12:08:20', 'admin@com', true, null, null,
        '$2a$10$cGWg.FaRYdb0DxZ3pgtTbeqlZQqBjJefzlRWk5JzJXBDuuFjvZBWm', '127.0.0.1', 'ADMIN', 'admin');
INSERT INTO users (id, created_at, email, enabled, last_login, last_login_ip, password, registration_ip, role, username)
VALUES (2, '2018-07-22 12:08:37', 'mod@com', true, null, null,
        '$2a$10$S0x7xukHJ9/jWLcHL841Rupm0ridM1cm4c3FtkJKG7c2nI9Mdkjia', '127.0.0.1', 'MODERATOR', 'moderator');
INSERT INTO users (id, created_at, email, enabled, last_login, last_login_ip, password, registration_ip, role, username)
VALUES (3, '2018-07-22 12:09:02', 'janitor@com', true, null, null,
        '$2a$10$HiOmNa0MzMAtUjNZqwxTEOijJIq2.hBEgS3FVakT8DKJ4dCcdpTr6', '127.0.0.1', 'JANITOR', 'janitor');
INSERT INTO users (id, created_at, email, enabled, last_login, last_login_ip, password, registration_ip, role, username)
VALUES (4, '2018-07-22 12:09:22', 'user@com', true, null, null,
        '$2a$10$.e5bI.TKWLgMmJVjC1X3d.kYn6U/kRu69.ZJ9ss0pLGULU6zh4M0.', '127.0.0.1', 'USER', 'user');
SET FOREIGN_KEY_CHECKS = 1;