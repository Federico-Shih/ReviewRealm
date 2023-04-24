ALTER TABLE users
    ADD COLUMN username varchar(24) DEFAULT ''
;

UPDATE users SET username = CONCAT(SPLIT_PART(email, '@', 1), '_', id);

ALTER TABLE users
    ALTER COLUMN username SET NOT NULL;

ALTER TABLE users
    ADD CONSTRAINT users_username_unique UNIQUE (username);