ALTER TABLE favoritegames ADD COLUMN IF NOT EXISTS reviewId integer;
ALTER TABLE favoritegames ADD CONSTRAINT favoritegames_reviewId_fkey FOREIGN KEY (reviewId) REFERENCES reviews(id);

INSERT INTO favoritegames (userId, gameId, reviewId)
SELECT authorid, gameId, id
FROM (
         SELECT authorId, gameId, id,
                ROW_NUMBER() OVER (PARTITION BY authorId ORDER BY rating DESC) AS row_num
         FROM reviews
         WHERE rating >7
     ) ranked_review
WHERE row_num <= 3;
