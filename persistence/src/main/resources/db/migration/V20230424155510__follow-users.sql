CREATE TABLE IF NOT EXISTS followers (
    id SERIAL PRIMARY KEY,
    userId int,
    following int,
    FOREIGN KEY(userId) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY(following) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT no_same_follow_check CHECK ( userId != following )
);

CREATE INDEX IF NOT EXISTS followersUserIdIndex on followers (userId);
CREATE INDEX IF NOT EXISTS followingUserIdIndex on followers (following);