ALTER TABLE users
    ADD COLUMN followers INTEGER DEFAULT 0;
ALTER TABLE users
    ADD COLUMN following INTEGER DEFAULT 0;

UPDATE users
SET followers = (SELECT COUNT(*) FROM followers WHERE followers.following = users.id);
UPDATE users
SET following = (SELECT COUNT(*) FROM followers WHERE followers.userid = users.id);

ALTER TABLE favoritegames
    ALTER COLUMN gameid TYPE BIGINT;
ALTER TABLE favoritegames
    ALTER COLUMN userid TYPE BIGINT;