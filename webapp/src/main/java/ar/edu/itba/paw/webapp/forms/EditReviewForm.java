package ar.edu.itba.paw.webapp.forms;

import ar.edu.itba.paw.enums.GamelengthUnit;
import ar.edu.itba.paw.models.Review;

public class EditReviewForm extends SubmitReviewForm {
    public EditReviewForm fromReview(Review review) {
        setCompleted(review.getCompleted());
        setDifficulty(review.getDifficulty() != null ? review.getDifficulty().toString() : "");
        setReviewContent(review.getContent());
        setReviewTitle(review.getTitle());
        setReviewRating(review.getRating());
        setPlatform(review.getPlatform() != null ? review.getPlatform().toString() : "");
        setReplayability(review.getReplayability() != null ? review.getReplayability() : false);
        if (review.getGameLength() != null) {
            if (review.getGameLength() > GamelengthUnit.HOURS.toSeconds(1.0)) {
                setGameLength(review.getGameLength() / GamelengthUnit.HOURS.toSeconds(1.0));
                setUnit(GamelengthUnit.HOURS.toString());
            } else {
                setGameLength(review.getGameLength() / GamelengthUnit.MINUTES.toSeconds(1.0));
                setUnit(GamelengthUnit.MINUTES.toString());
            }
        }
        return this;
    }
}
