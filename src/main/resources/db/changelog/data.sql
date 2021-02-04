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
("moderator4", "$2a$10$7GzzNFtE.3ed9MIoRghecO4ggsF42/tvItAgi3N4NFQtkC45yrE4i"),
("moderator5", "$2a$10$j6wO./.XCTem8jQUek2Q1.2UjfhjiJl4sygmehv4xkWNLcwlxR/xW");

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

/*INSERT INTO forum (title, parent_forum_id) VALUES
("main forum 1 of forums", null),
("main forum 2 of threads", null),
("main forum 3 of forums", null),
("main forum 4 of threads", null),
("sub-forum 1 in 1 of threads", 1),
("sub-forum 2 in 1 of forums", 1),
("sub-sub-forum 1 in 6 of forums", 6),
("sub-sub-forum 2 in 6 of forums", 6),
("sub-sub-forum 3 in 6 of forums", 6),
("sub-sub-forum 4 in 6 of forums", 6);

INSERT INTO forum_moderators (forum_id, user_id) VALUES
(1, 6),
(2, 6),
(2, 7),
(3, 7),
(4, 8),
(6, 9),
(7, 10);

INSERT INTO thread (title, message, creator_id, forum_id) VALUES
("user1 thread1", "user1 thread1 message", 1, 2),
("user1 thread2", "user1 thread2 message", 1, 2),
("user2 thread1", "user2 thread1 message", 2, 4),
("user3 thread1", "user3 thread1 message", 3, 5),
("admin1 thread1", "admin1 thread1 message", 4, 5);

INSERT INTO post (message, thread_id, creator_id) VALUES
("user1 thread1 message1", 1, 1),
("user1 thread1 message2", 1, 2),
("user1 thread1 message3", 1, 4),
("user1 thread1 message4", 1, 3),
("user1 thread1 message5", 1, 2),
("user1 thread1 message6", 1, 2),
("user1 thread1 message7", 1, 1),
("user1 thread1 message8", 1, 4),
("user1 thread1 message9", 1, 1),
("user1 thread2 message1", 2, 3),
("user1 thread2 message2", 2, 3),
("user1 thread2 message3", 2, 4),
("user1 thread2 message4", 2, 2),
("user1 thread2 message5", 2, 1),
("user1 thread2 message6", 2, 4),
("user1 thread2 message7", 2, 2),
("user1 thread2 message8", 2, 2),
("user2 thread1 message1", 3, 3),
("user2 thread1 message2", 3, 2),
("user2 thread1 message3", 3, 4),
("user2 thread1 message4", 3, 1),
("user3 thread1 message1", 4, 2),
("user3 thread1 message2", 4, 2),
("user3 thread1 message3", 4, 3);*/



























