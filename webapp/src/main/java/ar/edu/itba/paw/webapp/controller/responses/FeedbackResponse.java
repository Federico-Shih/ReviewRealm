package ar.edu.itba.paw.webapp.controller.responses;

import ar.edu.itba.paw.enums.Difficulty;
import ar.edu.itba.paw.enums.FeedbackType;
import ar.edu.itba.paw.enums.Platform;
import ar.edu.itba.paw.models.Review;

import javax.ws.rs.core.UriInfo;
import java.net.URI;

public class FeedbackResponse extends BaseResponse {

    private static final String BASE_PATH = "/reviews";
    private FeedbackType feedbackType;


    public static FeedbackResponse fromEntity(final UriInfo uri, long userId, long reviewId, FeedbackType feedbackType) {
        FeedbackResponse response = new FeedbackResponse();
        response.setFeedbackType(feedbackType);

        response.link("self", getLinkFromEntity(uri,userId, reviewId));
        response.link("liker", uri.getBaseUriBuilder().path("users").path(String.valueOf(userId)).build());
        response.link("review", uri.getBaseUriBuilder().path("reviews").path(String.valueOf(reviewId)).build());

        return response;
    }

    public static URI getLinkFromEntity(final UriInfo uri, long userId, long reviewId) {
        return uri.getBaseUriBuilder().path(BASE_PATH).path(String.format("/%s/feedback/%s",reviewId,userId)).build();
    }

    public FeedbackType getFeedbackType() {
        return feedbackType;
    }

    public void setFeedbackType(FeedbackType feedbackType) {
        this.feedbackType = feedbackType;
    }
}