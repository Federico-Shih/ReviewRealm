package ar.edu.itba.paw.enums;

import java.util.Optional;

public enum NotificationType {
    USER_I_FOLLOW_WRITES_REVIEW("userIFollowWritesReview", "notification.userIFollowWritesReview"),
    MY_REVIEW_IS_DELETED("myReviewIsDeleted", "notification.myReviewIsDeleted"),
    ;
    private final String typeName;
    private final String nameCode;

    NotificationType(String typeName, String nameCode) {
        this.typeName = typeName;
        this.nameCode = nameCode;
    }

    public String getTypeName() {
        return typeName;
    }

    public String getNameCode() {
        return nameCode;
    }

    public static Optional<NotificationType> getByTypeName(String typeName) {
        for (NotificationType notificationType : values()) {
            if (notificationType.typeName.equals(typeName)) {
                return Optional.of(notificationType);
            }
        }
        return Optional.empty();
    }
}
