package ar.edu.itba.paw.webapp.validators;

import ar.edu.itba.paw.enums.FeedbackType;
import org.glassfish.jersey.media.multipart.FormDataParam;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

public class FeedbackTypeForm {
    @Pattern(regexp ="^(LIKE|DISLIKE)$", message = "Pattern.reviewFeedback")
    @NotNull(message = "NotNull.property")
    private String feedbackType;

    public FeedbackTypeForm() {
    }

    public FeedbackType transformToEnum() {
        return FeedbackType.valueOf(feedbackType);
    }

    public void setFeedbackType(String feedbackType) {
        this.feedbackType = feedbackType;
    }

    public String getFeedbackType() {
        return this.feedbackType;
    }


}
