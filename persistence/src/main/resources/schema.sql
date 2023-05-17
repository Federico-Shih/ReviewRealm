CREATE TABLE IF NOT EXISTS users (
                                     id serial PRIMARY KEY ,
                                     email varchar(100) NOT NULL UNIQUE ,
                                     password varchar(100) NOT NULL
);

CREATE TABLE IF NOT EXISTS genres (
                                      id serial PRIMARY KEY ,
                                      name varchar(100) NOT NULL
);

CREATE TABLE IF NOT EXISTS games (
                                     id SERIAL primary key,
                                     name varchar(100) NOT NULL,
                                     description text,
                                     developer varchar(100) NOT NULL,
                                     publisher varchar(100) NOT NULL,
                                     imageUrl text NOT NULL,
                                     publishDate timestamp NOT NULL
);

CREATE TABLE IF NOT EXISTS genreForGames (
                                             genreId int,
                                             gameId int,
                                             FOREIGN KEY(gameId) REFERENCES games(id) ON DELETE CASCADE,
                                             PRIMARY KEY(genreId, gameId)
);

CREATE TABLE IF NOT EXISTS reviews(
                                      id serial PRIMARY KEY,
                                      authorId int NOT NULL,
                                      gameId int NOT NULL,
                                      title varchar(100) NOT NULL,
                                      content text NOT NULL,
                                      createdDate timestamp NOT NULL,
                                      rating int NOT NULL CHECK (rating > 0 and rating <= 10),
                                      FOREIGN KEY(authorId) REFERENCES users(id) ON DELETE CASCADE,
                                      FOREIGN KEY(gameId) REFERENCES games(id) ON DELETE CASCADE
);
ALTER TABLE reviews ADD COLUMN difficulty varchar(10) NULL;
ALTER TABLE reviews ADD COLUMN platform varchar(10) NULL;
ALTER TABLE reviews ADD COLUMN gamelength DOUBLE PRECISION NULL;
ALTER TABLE reviews ADD COLUMN replayability BOOLEAN NULL;
ALTER TABLE reviews ADD COLUMN completed BOOLEAN NULL;

ALTER TABLE users
    ADD COLUMN username varchar(24) DEFAULT '';

ALTER TABLE users
    ALTER COLUMN username SET NOT NULL;

ALTER TABLE users
    ADD CONSTRAINT users_username_unique UNIQUE (username);
CREATE TABLE IF NOT EXISTS followers (
                                id SERIAL PRIMARY KEY,
                                userId int,
                                following int,
                                FOREIGN KEY(userId) REFERENCES users(id) ON DELETE CASCADE,
                                FOREIGN KEY(following) REFERENCES users(id) ON DELETE CASCADE,
                                CONSTRAINT no_same_follow_check CHECK ( userId != following )
                                                           );

CREATE INDEX IF NOT EXISTS followersUserIdIndex on followers (userId);
CREATE INDEX IF NOT EXISTS followingUserIdIndex on followers (following);
CREATE TABLE IF NOT EXISTS roles (
      roleId SERIAL PRIMARY KEY,
      roleName varchar(20)
);

CREATE TABLE IF NOT EXISTS user_roles (
          userId int REFERENCES users(id),
          roleId int REFERENCES roles(roleId),
          constraint USER_ROLES_CONS unique (userId, roleId)
);

CREATE INDEX userroles_index ON user_roles(userId);
INSERT INTO roles (roleName) VALUES ('MODERATOR');
CREATE TABLE IF NOT EXISTS images(
          id varchar(16) PRIMARY KEY,
          data binary NOT NULL,
          mediatype varchar(50) NOT NULL
);
ALTER TABLE games
    ADD COLUMN imageId varchar(50);

ALTER TABLE games
    ADD CONSTRAINT imageForeignKey
        FOREIGN KEY (imageid)
            REFERENCES images(id);

ALTER TABLE games DROP COLUMN imageurl;
CREATE TABLE IF NOT EXISTS genreforusers (
      genreId integer,
      userId integer,
      PRIMARY KEY (genreId, userId),
      FOREIGN KEY (userId) REFERENCES users(id) ON DELETE CASCADE
    );

ALTER TABLE games
    ADD COLUMN ratingsum INT DEFAULT 0;
ALTER TABLE games
    ADD COLUMN reviewcount INT DEFAULT 0;

CREATE TABLE IF NOT EXISTS favoritegames (
     gameId integer,
     userId integer,
     PRIMARY KEY (gameId, userId),
     FOREIGN KEY (gameId) REFERENCES games(id) ON DELETE CASCADE,
     FOREIGN KEY (userId) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS tokens
(
    id SERIAL PRIMARY KEY ,
    token character varying(16) UNIQUE,
    password varchar(50),
    expiration timestamp,
    userid integer,
    CONSTRAINT userforeignkey FOREIGN KEY (userid)
        REFERENCES users (id)
        ON UPDATE NO ACTION
        ON DELETE CASCADE
);

ALTER TABLE users ADD COLUMN enabled boolean DEFAULT false;

CREATE TABLE IF NOT EXISTS reviewfeedback(
                                             reviewId INT         NOT NULL,
                                             userId   INT         NOT NULL,
                                             feedback VARCHAR(50) NOT NULL,
                                             FOREIGN KEY (reviewId) REFERENCES reviews (id) ON DELETE CASCADE,
                                             FOREIGN KEY (userId) REFERENCES users(id) ON DELETE CASCADE,
                                             PRIMARY KEY (reviewId, userId)
);

ALTER TABLE reviews ADD COLUMN likes INT DEFAULT 0;
ALTER TABLE reviews ADD COLUMN dislikes INT DEFAULT 0;

ALTER TABLE users ADD COLUMN reputation INT DEFAULT 0;
CREATE TABLE IF NOT EXISTS notifications (
       notificationId SERIAL PRIMARY KEY,
       notificationType VARCHAR(64) NOT NULL
);


CREATE TABLE IF NOT EXISTS user_disabled_notifications
(
    userId int REFERENCES users(id),
    notificationId int REFERENCES notifications(notificationId),
    PRIMARY KEY (userId, notificationId)
);

INSERT INTO notifications (notificationType) VALUES ('userIFollowWritesReview');
INSERT INTO notifications (notificationType) VALUES ('myReviewIsDeleted');

ALTER TABLE games ADD COLUMN suggestion boolean;
UPDATE games SET suggestion=false;
ALTER TABLE games ALTER COLUMN suggestion SET NOT NULL;

ALTER TABLE users ADD COLUMN avatar int;
UPDATE users SET avatar=0 WHERE users.avatar is null;
