ALTER TABLE favoritegames DROP CONSTRAINT IF EXISTS favoritegames_pkey;
ALTER TABLE favoritegames DROP COLUMN IF EXISTS reviewId;
ALTER TABLE favoritegames ADD CONSTRAINT favoritegames_pkey PRIMARY KEY(userid,gameid);