package ar.edu.itba.paw.models;

import ar.edu.itba.paw.enums.FeedbackType;
import ar.edu.itba.paw.models.keys.ReviewFeedbackId;

import javax.persistence.*;

@Entity
@Table(name = "reviewfeedback")
@IdClass(ReviewFeedbackId.class)
public class ReviewFeedback {
    @Id
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "userid", referencedColumnName = "id")
    private User user;

    @Id
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "reviewid", referencedColumnName = "id")
    private Review review;

    @Column(name = "feedback")
    @Enumerated(EnumType.STRING)
    private FeedbackType feedback;

    protected ReviewFeedback() {
    }

    public ReviewFeedback(User user, Review review, FeedbackType feedback) {
        this.user = user;
        this.review = review;
        this.feedback = feedback;
    }

    public Review getReview() {
        return review;
    }

    public User getUser() {
        return user;
    }

    public FeedbackType getFeedback() {
        return feedback;
    }

    public void setFeedback(FeedbackType feedback) {
        this.feedback = feedback;
    }
}
