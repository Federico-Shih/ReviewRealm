ALTER TABLE users ADD COLUMN IF NOT EXISTS avatar int;
UPDATE users SET avatar=0 WHERE users.avatar is null;