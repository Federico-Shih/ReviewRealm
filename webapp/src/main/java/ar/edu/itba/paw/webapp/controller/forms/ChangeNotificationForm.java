package ar.edu.itba.paw.webapp.controller.forms;

import ar.edu.itba.paw.enums.NotificationType;

import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.Map;

public class ChangeNotificationForm {
    @NotNull(message = "NotNull.property")
    private Boolean userIFollowWritesReview;

    @NotNull(message = "NotNull.property")
    private Boolean myReviewIsDeleted;

    public boolean getUserIFollowWritesReview() {
        return userIFollowWritesReview;
    }

    public void setUserIFollowWritesReview(boolean userIFollowWritesReview) {
        this.userIFollowWritesReview = userIFollowWritesReview;
    }

    public boolean getMyReviewIsDeleted() {
        return myReviewIsDeleted;
    }

    public void setMyReviewIsDeleted(boolean myReviewIsDeleted) {
        this.myReviewIsDeleted = myReviewIsDeleted;
    }

    public Map<NotificationType, Boolean> getNotificationMap() {
        Map<NotificationType, Boolean> notificationMap = new HashMap<>();
        notificationMap.put(NotificationType.USER_I_FOLLOW_WRITES_REVIEW, userIFollowWritesReview);
        notificationMap.put(NotificationType.MY_REVIEW_IS_DELETED, myReviewIsDeleted);
        return notificationMap;
    }
}
