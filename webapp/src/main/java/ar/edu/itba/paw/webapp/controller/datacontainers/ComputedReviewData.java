package ar.edu.itba.paw.webapp.controller.datacontainers;

import ar.edu.itba.paw.enums.GamelengthUnit;
import ar.edu.itba.paw.models.Review;

public class ComputedReviewData {
    private final Double gametime;

    private final GamelengthUnit unit;

    public static ComputedReviewData factory(Review review) {
        if (review == null || review.getGameLength() == null) return null;
        return new ComputedReviewData(review);
    }

    private ComputedReviewData(Review review) {
        if (review == null || review.getGameLength() == null)
            throw new IllegalStateException();
        if (review.getGameLength() > GamelengthUnit.HOURS.toSeconds(1.0)) {
            this.gametime = review.getGameLength() / GamelengthUnit.HOURS.toSeconds(1.0);
            this.unit = GamelengthUnit.HOURS;
        } else {
            this.gametime = review.getGameLength() / GamelengthUnit.MINUTES.toSeconds(1.0);
            this.unit = GamelengthUnit.MINUTES;
        }
    }

    public Double getGametime() {
        return Math.round(gametime * 100) / 100.0;
    }

    public GamelengthUnit getUnit() {
        return unit;
    }

}