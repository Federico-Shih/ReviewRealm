
ALTER TABLE games ADD COLUMN suggestion boolean;
UPDATE games SET suggestion=false;
ALTER TABLE games ALTER COLUMN suggestion SET NOT NULL;
