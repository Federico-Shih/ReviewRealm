ALTER TABLE followers
    ALTER COLUMN userid TYPE BIGINT;

ALTER TABLE followers
    ALTER COLUMN following TYPE BIGINT;

ALTER TABLE tokens
    ALTER COLUMN userid TYPE BIGINT;

ALTER TABLE tokens
    DROP COLUMN password;