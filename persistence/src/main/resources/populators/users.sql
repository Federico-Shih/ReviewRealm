INSERT INTO users (id, username, email, password, enabled, reputation, avatar, language, xp, followers,following)
VALUES (1, 'username1', 'email', 'password', 1, 100, 1, 'en', 100, 0, 0);
INSERT INTO users (id, username, email, password, enabled, reputation, avatar, language, xp, followers,following)
VALUES (2, 'username2', 'email2', 'password', 1, 100, 1, 'en', 100, 0, 0);
INSERT INTO users (id, username, email, password, enabled, reputation, avatar, language, xp, followers,following)
VALUES (3, 'aaaa', 'email3', 'password', 1, 100, 1, 'en', 100, 0, 0);
INSERT INTO users (id, username, email, password, enabled, reputation, avatar, language, xp, followers,following)
VALUES (4, 'bbbb', 'email4', 'password', 1, 100, 1, 'en', 100, 0, 0);
INSERT INTO users (id, username, email, password, enabled, reputation, avatar, language, xp, followers,following)
VALUES (5, 'ab', 'email5', 'password', 1, 100, 1, 'en', 200, 0, 0);

ALTER SEQUENCE users_id_seq RESTART WITH 6;

INSERT INTO genreforusers (genreid, userid)
VALUES (1, 1);
INSERT INTO genreforusers (genreid, userid)
VALUES (2, 1);
INSERT INTO genreforusers (genreid, userid)
VALUES (3, 2);
INSERT INTO genreforusers (genreid, userid)
VALUES (2, 2);

INSERT INTO user_disabled_notifications (userid, notification)
VALUES (5, 'userIFollowWritesReview');

INSERT INTO user_roles (userid, role)
VALUES (4, 'MODERATOR');

INSERT INTO mission_progress (userid, mission, progress, startdate, times) VALUES (1, 'SETUP_PREFERENCES', 1, DATE '2019-01-01', 1);