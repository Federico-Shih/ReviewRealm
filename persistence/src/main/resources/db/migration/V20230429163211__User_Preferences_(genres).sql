CREATE TABLE IF NOT EXISTS genreforusers (
                                             genreId integer,
                                             userId integer,
                                             PRIMARY KEY (genreId, userId),
                                             FOREIGN KEY (genreId) REFERENCES genres(id) ON DELETE CASCADE,
                                             FOREIGN KEY (userId) REFERENCES users(id) ON DELETE CASCADE
);