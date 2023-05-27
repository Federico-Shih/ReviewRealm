UPDATE games
SET ratingsum   = 0,
    reviewcount = 0
WHERE ratingsum IS NULL
   or reviewcount IS NULL;