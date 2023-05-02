CREATE TABLE IF NOT EXISTS favoritegames (
                                             gameId integer,
                                             userId integer,
                                             reviewId integer,
                                             PRIMARY KEY (reviewId),
                                             FOREIGN KEY (gameId) REFERENCES games(id) ON DELETE CASCADE,
                                             FOREIGN KEY (userId) REFERENCES users(id) ON DELETE CASCADE,
                                             FOREIGN KEY (reviewId) REFERENCES reviews(id) ON DELETE CASCADE
);

INSERT INTO favoritegames (userId, gameId, reviewId)
SELECT authorid, gameId, id
FROM (
         SELECT authorId, gameId, id,
                ROW_NUMBER() OVER (PARTITION BY authorId ORDER BY rating DESC) AS row_num
         FROM reviews
         WHERE rating >7
     ) ranked_review
WHERE row_num <= 3;
