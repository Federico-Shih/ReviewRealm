CREATE TABLE IF NOT EXISTS reviewfeedback(
    reviewId INT         NOT NULL,
    userId   INT         NOT NULL,
    feedback VARCHAR(50) NOT NULL,
    FOREIGN KEY (reviewId) REFERENCES reviews (id) ON DELETE CASCADE,
    FOREIGN KEY (userId) REFERENCES users(id) ON DELETE CASCADE,
    PRIMARY KEY (reviewId, userId)
);

ALTER TABLE reviews ADD COLUMN likes INT DEFAULT 0;
ALTER TABLE reviews ADD COLUMN dislikes INT DEFAULT 0;

ALTER TABLE users ADD COLUMN reputation INT DEFAULT 0;