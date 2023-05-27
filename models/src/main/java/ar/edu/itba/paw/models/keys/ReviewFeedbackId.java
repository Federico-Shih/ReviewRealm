package ar.edu.itba.paw.models.keys;

import ar.edu.itba.paw.models.Review;
import ar.edu.itba.paw.models.User;

import java.io.Serializable;

public class ReviewFeedbackId implements Serializable {
    private User user;

    private Review review;

    public ReviewFeedbackId(User user, Review review) {
        this.user = user;
        this.review = review;
    }

    protected ReviewFeedbackId() {
        // For hibernate
    }

    public User getUser() {
        return user;
    }

    public Review getReview() {
        return review;
    }

    @Override
    public int hashCode() {
        return user.hashCode() + review.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (!(obj instanceof ReviewFeedbackId)) return false;
        ReviewFeedbackId other = (ReviewFeedbackId) obj;
        return other.user.equals(this.user) && other.review.equals(this.review);
    }
}
