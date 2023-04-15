package ar.edu.itba.paw.webapp.form;

import javax.validation.constraints.*;

public class SubmitReviewForm {

    @Size(min=8, max=100)
    private String reviewTitle;

    @Size(min=8, max=420)
    private String reviewContent;

    @Email
    private String reviewAuthor;

    @Max(value=10)
    @Min(value=1)
    private Integer reviewRating;

    public String getReviewTitle() {
        return reviewTitle;
    }

    public String getReviewContent() {
        return reviewContent;
    }

    public void setReviewTitle(String reviewTitle) {
        this.reviewTitle = reviewTitle;
    }

    public void setReviewContent(String reviewContent) {
        this.reviewContent = reviewContent;
    }

    public String getReviewAuthor() {
        return reviewAuthor;
    }

    public void setReviewAuthor(String reviewAuthor) {
        this.reviewAuthor = reviewAuthor;
    }

    public Integer getReviewRating() {
        return reviewRating;
    }

    public void setReviewRating(Integer reviewRating) {
        this.reviewRating = reviewRating;
    }
}
