INSERT INTO authority (authority) VALUES
("USER"),
("MODERATOR"),
("HEAD_MODERATOR"),
("ADMIN");

INSERT INTO user (username, password) VALUES
("user1", "$2a$10$VOkW9NCOy6UZx3/Om2y.zuj1i1xZQrCEXJsez2V.CkjEuSeoNqWky"),
("user2", "$2a$10$4nf6RBXk.Z59NAdmS76t8.MuLL/ibOopmVL.Lnf9rGsAXaEC0s73O"),
("user3", "$2a$10$H87xIZj7f3JDD2.fejASR.fXrp091Qd2Dtt.hsDOUbjoMrp7VoT76"),
("admin1", "$2a$10$1K2H99LDbrmz4GLj.KRmreYZrlC5eqbZ79.CgPXv.WS1DQ5fdiwhy"),
("headModerator1", "$2a$10$t7rpw/VndwDobDLdHe5SeeJ9xsd6QbPW/0mDiN1Xe2WFFefaNG/4y"),
("moderator1", "$2a$10$GplnWpXcAZolUkGKhW4URucIxtlYUycDyz92nu/rmj3vk.WtCijka"),
("moderator2", "$2a$10$2uXMzRSUu0oRg8ghXGNRIOB6Lswptct.ay1EcJP1QAcr29EamWSi6"),
("moderator3", "$2a$10$GiqY7E5.a5VIDtrwgdZms.YS07W2X5kVqawMgYEctTJRLvHqhWMdK"),
("moderator4", "$2a$10$7GzzNFtE.3ed9MIoRghecO4ggsF42/tvItAgi3N4NFQtkC45yrE4i");

INSERT INTO user_authorities (user_id, authority_id) VALUES
(1, 1),
(2, 1),
(3, 1),
(4, 1),
(4, 4),
(5, 1),
(5, 3),
(6, 1),
(6, 2),
(7, 1),
(7, 2),
(8, 1),
(8, 2),
(9, 1),
(9, 2);

INSERT INTO category (title, parent_category_id) VALUES
("main category 1 of categories", null),
("main category 2 of topics", null),
("main category 3 of categories", null),
("main category 4 of topics", null),
("sub-category 1 in 1 of topics", 1),
("sub-category 2 in 1 of categories", 1),
("sub-sub-category 1 in 6 of categories", 6),
("sub-sub-category 2 in 6 of categories", 6),
("sub-sub-category 3 in 6 of categories", 6),
("sub-sub-category 4 in 6 of categories", 6);

INSERT INTO category_moderators (category_id, user_id) VALUES
(1, 6),
(2, 6),
(2, 7),
(3, 7),
(4, 8),
(6, 9);

INSERT INTO topic (header, content, user_id, category_id) VALUES
("user1 topic1", "user1 topic1 content", 1, 2),
("user1 topic2", "user1 topic2 content", 1, 2),
("user2 topic1", "user2 topic1 content", 2, 4),
("user3 topic1", "user3 topic1 content", 3, 5),
("admin1 topic1", "admin1 topic1 content", 4, 5);

INSERT INTO comment (content, topic_id, user_id) VALUES
("user1 topic1 comment1", 1, 1),
("user1 topic1 comment2", 1, 2),
("user1 topic1 comment3", 1, 4),
("user1 topic1 comment4", 1, 3),
("user1 topic1 comment5", 1, 2),
("user1 topic1 comment6", 1, 2),
("user1 topic1 comment7", 1, 1),
("user1 topic1 comment8", 1, 4),
("user1 topic1 comment9", 1, 1),
("user1 topic2 comment1", 2, 3),
("user1 topic2 comment2", 2, 3),
("user1 topic2 comment3", 2, 4),
("user1 topic2 comment4", 2, 2),
("user1 topic2 comment5", 2, 1),
("user1 topic2 comment6", 2, 4),
("user1 topic2 comment7", 2, 2),
("user1 topic2 comment8", 2, 2),
("user2 topic1 comment1", 3, 3),
("user2 topic1 comment2", 3, 2),
("user2 topic1 comment3", 3, 4),
("user2 topic1 comment4", 3, 1),
("user3 topic1 comment1", 4, 2),
("user3 topic1 comment2", 4, 2),
("user3 topic1 comment3", 4, 3);



























