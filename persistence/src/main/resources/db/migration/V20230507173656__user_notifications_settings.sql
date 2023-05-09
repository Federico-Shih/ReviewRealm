CREATE TABLE IF NOT EXISTS notifications (
    notificationId SERIAL PRIMARY KEY,
    notificationType VARCHAR(64) NOT NULL
);

CREATE TABLE IF NOT EXISTS user_disabled_notifications
(
    userId int REFERENCES users(id) NOT NULL,
    notificationId int REFERENCES notifications(notificationId) NOT NULL,
    PRIMARY KEY (userId, notificationId)
);

INSERT INTO notifications (notificationType) VALUES ('userIFollowWritesReview');
INSERT INTO notifications (notificationType) VALUES ('myReviewIsDeleted');