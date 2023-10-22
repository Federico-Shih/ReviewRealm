package ar.edu.itba.paw.webapp.controller.forms;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@NotNull(message = "error.body.empty")
public class SubmitReportForm {
    @NotNull(message = "NotNull.property")
    private Long reviewId;
    @Pattern(regexp ="^(disrespectful|spam|irrelevant|spoiler|piracy|privacy)$", flags = Pattern.Flag.CASE_INSENSITIVE)
    @NotNull(message = "NotNull.property")
    private String reason;

    public Long getReviewId() {
        return reviewId;
    }

    public void setReviewId(Long reviewId) {
        this.reviewId = reviewId;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
