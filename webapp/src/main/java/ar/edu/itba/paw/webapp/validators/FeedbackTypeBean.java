package ar.edu.itba.paw.webapp.validators;

import ar.edu.itba.paw.enums.FeedbackType;

import javax.validation.constraints.Pattern;
import javax.ws.rs.QueryParam;

public class FeedbackTypeBean {
    @Pattern(regexp ="^(LIKE|DISLIKE)$")
    @QueryParam("feedbackType")
    private String feedbackType;

    public FeedbackTypeBean() {
    }

    public FeedbackType transformToEnum() {
        return FeedbackType.valueOf(feedbackType);
    }
}
