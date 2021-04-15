INSERT INTO authority (name) VALUES
('MODERATOR'),
('HEAD_MODERATOR'),
('ADMIN');

INSERT INTO user (username, password, join_time, authority_id) VALUES
('user1', '$2a$10$VOkW9NCOy6UZx3/Om2y.zuj1i1xZQrCEXJsez2V.CkjEuSeoNqWky', '2021-02-07 14:23:58', null),
('user2', '$2a$10$4nf6RBXk.Z59NAdmS76t8.MuLL/ibOopmVL.Lnf9rGsAXaEC0s73O', '2021-02-07 14:23:58', null),
('user3', '$2a$10$H87xIZj7f3JDD2.fejASR.fXrp091Qd2Dtt.hsDOUbjoMrp7VoT76', '2021-02-07 14:23:58', null),
('admin1', '$2a$10$1K2H99LDbrmz4GLj.KRmreYZrlC5eqbZ79.CgPXv.WS1DQ5fdiwhy', '2021-02-07 14:23:58', 3),
('admin2', '$2a$10$YfrUMUtbQzEsjnUF22e5ReV9cigri2XgIJvocmLoT5y81GGO.hzCu', '2021-02-07 14:23:58', 3),
('headModerator1', '$2a$10$t7rpw/VndwDobDLdHe5SeeJ9xsd6QbPW/0mDiN1Xe2WFFefaNG/4y', '2021-02-07 14:23:58', 2),
('moderator1', '$2a$10$GplnWpXcAZolUkGKhW4URucIxtlYUycDyz92nu/rmj3vk.WtCijka', '2021-02-07 14:23:58', 1),
('moderator2', '$2a$10$2uXMzRSUu0oRg8ghXGNRIOB6Lswptct.ay1EcJP1QAcr29EamWSi6', '2021-02-07 14:23:58', 1),
('moderator3', '$2a$10$2uXMzRSUu0oRg8ghXGNRIOB6Lswptct.ay1EcJP1QAcr29EamWSi6', '2021-02-07 14:23:58', 1),
('user4', '$2a$10$H87xIZj7f3JDD2.fejASR.fXrp091Qd2Dtt.hsDOUbjoMrp7VoT76', '2021-02-07 14:23:58', null),
('user5', '$2a$10$H87xIZj7f3JDD2.fejASR.fXrp091Qd2Dtt.hsDOUbjoMrp7VoT76', '2021-02-07 14:23:58', 3),
('user6', '$2a$10$H87xIZj7f3JDD2.fejASR.fXrp091Qd2Dtt.hsDOUbjoMrp7VoT76', '2021-02-07 14:23:58', 2),
('user7', '$2a$10$H87xIZj7f3JDD2.fejASR.fXrp091Qd2Dtt.hsDOUbjoMrp7VoT76', '2021-02-07 14:23:58', 1),
('user8', '$2a$10$H87xIZj7f3JDD2.fejASR.fXrp091Qd2Dtt.hsDOUbjoMrp7VoT76', '2021-02-07 14:23:58', null),
('user9', '$2a$10$H87xIZj7f3JDD2.fejASR.fXrp091Qd2Dtt.hsDOUbjoMrp7VoT76', '2021-02-07 14:23:58', 1),
('headModerator2', '$2a$10$t7rpw/VndwDobDLdHe5SeeJ9xsd6QbPW/0mDiN1Xe2WFFefaNG/4y', '2021-02-07 14:23:58', 2),
('headModerator3', '$2a$10$t7rpw/VndwDobDLdHe5SeeJ9xsd6QbPW/0mDiN1Xe2WFFefaNG/4y', '2021-02-07 14:23:58', 2),
('moderator4', '$2a$10$2uXMzRSUu0oRg8ghXGNRIOB6Lswptct.ay1EcJP1QAcr29EamWSi6', '2021-02-07 14:23:58', 1);

INSERT INTO forum (title, description, parent_forum_id, create_time) VALUES
('main forum 1', 'description', null, '2021-02-07 14:23:58'),
('main forum 2', 'description', null, '2021-02-07 14:23:58'),
('sub forum 1 in 1', 'description', 1, '2021-02-07 14:23:58'),
('sub forum 2 in 1', 'description', 1, '2021-02-07 14:23:58'),
('sub sub forum 1 in 1', 'description', 3, '2021-02-07 14:23:58'),
('sub forum 1 in 2', 'description', 2, '2021-02-07 14:23:58');

INSERT INTO forum_moderators (forum_id, user_id) VALUES
(1, 7),
(6, 7),
(3, 8),
(3, 9),
(1, 13),
(6, 13),
(3, 15),
(2, 18);

INSERT INTO thread (title, message, creator_id, forum_id, create_time, locked) VALUES
('thread 1 title', 'thread message', 1, 3, '2021-02-07 14:23:58', false),
('thread 2 title', 'thread message', 2, 3, '2021-02-07 14:23:58', true),
('thread 3 title', 'thread message', 1, 4, '2021-02-07 14:23:58', false),
('thread 4 title', 'thread message', 2, 5, '2021-02-07 14:23:58', false),
('thread 5 title', 'thread message', 2, 5, '2021-02-07 14:23:58', false),
('thread 6 title', 'thread message', 3, 6, '2021-02-07 14:23:58', true);

INSERT INTO post (message, thread_id, creator_id, create_time) VALUES
('post 1 message', 1, 2, '2021-02-07 14:23:58'),
('post 2 message', 1, 2, '2021-02-07 14:23:58'),
('post 3 message', 1, 3, '2021-02-07 14:23:58'),
('post 4 message', 1, 4, '2021-02-07 14:23:58'),
('post 5 message', 2, 1, '2021-02-07 14:23:58'),
('post 6 message', 2, 2, '2021-02-07 14:23:58'),
('post 7 message', 2, 3, '2021-02-07 14:23:58'),
('post 8 message', 3, 1, '2021-02-07 14:23:58'),
('post 9 message', 3, 1, '2021-02-07 14:23:58'),
('post 10 message', 4, 2, '2021-02-07 14:23:58'),
('post 11 message', 4, 2, '2021-02-07 14:23:58'),
('post 12 message', 4, 4, '2021-02-07 14:23:58'),
('post 13 message', 5, 5, '2021-02-07 14:23:58'),
('post 14 message', 5, 6, '2021-02-07 14:23:58'),
('post 15 message', 6, 5, '2021-02-07 14:23:58'),
('post 16 message', 1, 4, '2021-02-07 14:23:58'),
('post 17 message', 1, 3, '2021-02-07 14:23:58'),
('post 18 message', 1, 2, '2021-02-07 14:23:58'),
('post 19 message', 1, 4, '2021-02-07 14:23:58');

INSERT INTO post_likes (user_id, post_id) VALUES
(1, 1),
(2, 1),
(3, 1),
(4, 1),
(5, 1),
(3, 2),
(3, 3),
(2, 3),
(4, 3),
(6, 3),
(5, 3);

INSERT INTO thread_likes (user_id, thread_id) VALUES
(1, 1),
(2, 1),
(3, 1),
(4, 1),
(5, 1),
(6, 1),
(1, 2),
(2, 2),
(3, 2),
(4, 2),
(4, 4),
(2, 4),
(6, 4);



























