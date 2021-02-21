INSERT INTO authority (authority) VALUES
("USER"),
("MODERATOR"),
("HEAD_MODERATOR"),
("ADMIN");

INSERT INTO user (username, password, join_time) VALUES
("user1", "$2a$10$VOkW9NCOy6UZx3/Om2y.zuj1i1xZQrCEXJsez2V.CkjEuSeoNqWky", "2021-02-07 14:23:58"),
("user2", "$2a$10$4nf6RBXk.Z59NAdmS76t8.MuLL/ibOopmVL.Lnf9rGsAXaEC0s73O", "2021-02-07 14:23:58"),
("user3", "$2a$10$H87xIZj7f3JDD2.fejASR.fXrp091Qd2Dtt.hsDOUbjoMrp7VoT76", "2021-02-07 14:23:58"),
("admin1", "$2a$10$1K2H99LDbrmz4GLj.KRmreYZrlC5eqbZ79.CgPXv.WS1DQ5fdiwhy", "2021-02-07 14:23:58"),
("headModerator1", "$2a$10$t7rpw/VndwDobDLdHe5SeeJ9xsd6QbPW/0mDiN1Xe2WFFefaNG/4y", "2021-02-07 14:23:58"),
("moderator1", "$2a$10$GplnWpXcAZolUkGKhW4URucIxtlYUycDyz92nu/rmj3vk.WtCijka", "2021-02-07 14:23:58"),
("moderator2", "$2a$10$2uXMzRSUu0oRg8ghXGNRIOB6Lswptct.ay1EcJP1QAcr29EamWSi6", "2021-02-07 14:23:58"),
("moderator3", "$2a$10$GiqY7E5.a5VIDtrwgdZms.YS07W2X5kVqawMgYEctTJRLvHqhWMdK", "2021-02-07 14:23:58"),
("moderator4", "$2a$10$7GzzNFtE.3ed9MIoRghecO4ggsF42/tvItAgi3N4NFQtkC45yrE4i", "2021-02-07 14:23:58"),
("moderator5", "$2a$10$j6wO./.XCTem8jQUek2Q1.2UjfhjiJl4sygmehv4xkWNLcwlxR/xW", "2021-02-07 14:23:58");

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
(9, 2),
(10, 1),
(10, 2);

INSERT INTO forum (title, description, parent_forum_id, create_time) VALUES
("main forum 1", "description", null, "2021-02-07 14:23:58"),
("main forum 2", "description", null, "2021-02-07 14:23:58"),
("main forum 3", "description", null, "2021-02-07 14:23:58"),
("main forum 4", "description", null, "2021-02-07 14:23:58"),
("sub-forum 1 in 1", "description", 1, "2021-02-07 14:23:58"),
("sub-forum 2 in 1", "description", 1, "2021-02-07 14:23:58"),
("sub-forum 1 in 2", "description", 2, "2021-02-07 14:23:58"),
("sub-forum 2 in 2", "description", 2, "2021-02-07 14:23:58"),
("sub-forum 1 in 4", "description", 4, "2021-02-07 14:23:58"),
("sub-forum 2 in 4", "description", 4, "2021-02-07 14:23:58"),
("sub-sub-forum 1 in 1", "description", 5, "2021-02-07 14:23:58"),
("sub-sub-forum 2 in 1", "description", 5, "2021-02-07 14:23:58"),
("sub-sub-forum 1 in 2", "description", 10, "2021-02-07 14:23:58");

/*INSERT INTO forum_moderators (forum_id, user_id) VALUES*/


INSERT INTO thread (title, message, creator_id, forum_id, create_time, locked) VALUES
("user1 thread1", "message", 1, 5, "2021-02-07 14:23:58", false),
("user2 thread2", "message", 2, 6, "2021-02-07 14:23:58", false),
("user3 thread3", "message", 3, 7, "2021-02-07 14:23:58", false),
("user2 thread4", "message", 2, 11, "2021-02-07 14:23:58", false),
("user2 thread5", "message", 2, 11, "2021-02-07 14:23:58", false),
("user2 thread6", "message", 2, 11, "2021-02-07 14:23:58", false);

INSERT INTO post (message, thread_id, creator_id, create_time) VALUES
("user3 post1", 1, 3, "2021-02-07 14:23:58"),
("user3 post1", 1, 3, "2021-02-07 14:23:58"),
("user3 post1", 1, 3, "2021-02-07 14:23:58"),
("user2 post3", 3, 2, "2021-02-07 14:23:58"),
("user2 post3", 3, 2, "2021-02-07 14:23:58"),
("user2 post1", 4, 2, "2021-02-07 14:23:58"),
("user2 post2", 4, 2, "2021-02-07 14:23:58"),
("user1 post3", 4, 1, "2021-02-07 14:23:58"),
("user1 post4", 4, 1, "2021-02-07 14:23:58"),
("user1 post5", 1, 1, "2021-02-07 14:23:58"),
("user1 post6", 1, 1, "2021-02-07 14:23:58"),
("user1 post7", 1, 1, "2021-02-07 14:23:58"),
("user1 post8", 1, 1, "2021-02-07 14:23:58"),
("user1 post9", 1, 1, "2021-02-07 14:23:58"),
("user1 post10", 1, 1, "2021-02-07 14:23:58"),
("user1 post11", 1, 1, "2021-02-07 14:23:58"),
("user1 post12", 1, 1, "2021-02-07 14:23:58"),
("user1 post13", 1, 1, "2021-02-07 14:23:58");




























