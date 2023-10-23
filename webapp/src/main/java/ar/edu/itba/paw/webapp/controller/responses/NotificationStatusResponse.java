package ar.edu.itba.paw.webapp.controller.responses;

import ar.edu.itba.paw.enums.NotificationType;
import ar.edu.itba.paw.models.User;

import javax.ws.rs.core.UriInfo;

import static ar.edu.itba.paw.webapp.controller.responses.NotificationResponse.getLinkFromEntity;

public class NotificationStatusResponse extends BaseResponse {
    private NotificationTypeResponse userIFollowWritesReview;
    private NotificationTypeResponse myReviewIsDeleted;

    public static NotificationStatusResponse fromEntity(final UriInfo uri, User user) {
        NotificationStatusResponse response = new NotificationStatusResponse();
        for (NotificationType type : NotificationType.values()) {
            switch (type) {
                case USER_I_FOLLOW_WRITES_REVIEW:
                    response.userIFollowWritesReview = NotificationTypeResponse.fromEntity(uri, type, !user.getDisabledNotifications().contains(type));
                    break;
                case MY_REVIEW_IS_DELETED:
                    response.myReviewIsDeleted = NotificationTypeResponse.fromEntity(uri, type, !user.getDisabledNotifications().contains(type));
                    break;
            }
        }
        response.link("self", uri.getBaseUriBuilder().path("users").path(String.valueOf(user.getId())).path("notifications").build());
        return response;
    }

    public NotificationTypeResponse getUserIFollowWritesReview() {
        return userIFollowWritesReview;
    }

    public NotificationTypeResponse getMyReviewIsDeleted() {
        return myReviewIsDeleted;
    }

    private static class NotificationTypeResponse extends BaseResponse {
        private final boolean enabled;
        private final String type;

        public NotificationTypeResponse(boolean enabled, String type) {
            this.enabled = enabled;
            this.type = type;
        }

        public static NotificationTypeResponse fromEntity(UriInfo uri, NotificationType type, boolean enabled) {
            NotificationTypeResponse response = new NotificationTypeResponse(enabled, type.getTypeName());
            response.link("self", getLinkFromEntity(uri, type));
            return response;
        }

        public boolean isEnabled() {
            return enabled;
        }

        public String getType() {
            return type;
        }
    }
}
