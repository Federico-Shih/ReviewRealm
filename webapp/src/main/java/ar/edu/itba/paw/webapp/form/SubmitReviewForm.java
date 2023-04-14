package ar.edu.itba.paw.webapp.form;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

public class SubmitReviewForm {

    @Size(min=8, max=100)
    @Pattern(regexp = "^[a-zA-Z0-9!¡¿?$&/#]+$")
    private String reviewTitle;

    @Size(min=10, max=420)
    private String reviewContent;

    @Pattern(regexp = "@")
    private String reviewAuthor;

    @Size(max=10)
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
