INSERT INTO authority (id, authority) VALUES
(null, "USER"),
(null, "ADMIN");

INSERT INTO user (id, username, password) VALUES
(null, "user1", "$2a$10$VOkW9NCOy6UZx3/Om2y.zuj1i1xZQrCEXJsez2V.CkjEuSeoNqWky"),
(null, "user2", "$2a$10$4nf6RBXk.Z59NAdmS76t8.MuLL/ibOopmVL.Lnf9rGsAXaEC0s73O"),
(null, "user3", "$2a$10$H87xIZj7f3JDD2.fejASR.fXrp091Qd2Dtt.hsDOUbjoMrp7VoT76"),
(null, "admin1", "$2a$10$1K2H99LDbrmz4GLj.KRmreYZrlC5eqbZ79.CgPXv.WS1DQ5fdiwhy");

INSERT INTO user_authorities (user_id, authority_id) VALUES
(1, 1),
(2, 1),
(3, 1),
(4, 1),
(4, 2);

INSERT INTO category (id, title, parent_category_id) VALUES
(null, "main category 1 of categories", null),
(null, "main category 2 of topics", null),
(null, "main category 3 of categories", null),
(null, "main category 4 of topics", null),
(null, "sub-category 1 in 1 of topics", 1),
(null, "sub-category 2 in 1 of categories", 1),
(null, "sub-sub-category 1 in 6 of categories", 6),
(null, "sub-sub-category 2 in 6 of categories", 6),
(null, "sub-sub-category 3 in 6 of categories", 6),
(null, "sub-sub-category 4 in 6 of categories", 6);

INSERT INTO topic (id, header, content, user_id, category_id) VALUES
(null, "user1 topic1", "user1 topic1 content", 1, 2),
(null, "user1 topic2", "user1 topic2 content", 1, 2),
(null, "user2 topic1", "user2 topic1 content", 2, 4),
(null, "user3 topic1", "user3 topic1 content", 3, 5),
(null, "admin1 topic1", "admin1 topic1 content", 4, 5);

INSERT INTO comment (id, content, topic_id, user_id) VALUES
(null, "user1 topic1 comment1", 1, 1),
(null, "user1 topic1 comment2", 1, 2),
(null, "user1 topic1 comment3", 1, 4),
(null, "user1 topic1 comment4", 1, 3),
(null, "user1 topic1 comment5", 1, 2),
(null, "user1 topic1 comment6", 1, 2),
(null, "user1 topic1 comment7", 1, 1),
(null, "user1 topic1 comment8", 1, 4),
(null, "user1 topic1 comment9", 1, 1),
(null, "user1 topic2 comment1", 2, 3),
(null, "user1 topic2 comment2", 2, 3),
(null, "user1 topic2 comment3", 2, 4),
(null, "user1 topic2 comment4", 2, 2),
(null, "user1 topic2 comment5", 2, 1),
(null, "user1 topic2 comment6", 2, 4),
(null, "user1 topic2 comment7", 2, 2),
(null, "user1 topic2 comment8", 2, 2),
(null, "user2 topic1 comment1", 3, 3),
(null, "user2 topic1 comment2", 3, 2),
(null, "user2 topic1 comment3", 3, 4),
(null, "user2 topic1 comment4", 3, 1),
(null, "user3 topic1 comment1", 4, 2),
(null, "user3 topic1 comment2", 4, 2),
(null, "user3 topic1 comment3", 4, 3);



























