package ar.edu.itba.paw.webapp.controller.forms;

import ar.edu.itba.paw.enums.Difficulty;
import ar.edu.itba.paw.enums.GamelengthUnit;
import ar.edu.itba.paw.enums.Platform;
import ar.edu.itba.paw.models.Review;
import ar.edu.itba.paw.webapp.exceptions.UnitNotSelectedException;

import javax.validation.constraints.*;

public class EditReviewForm {

    @Size(min=8, max=100, message="Size.submitReview.title")
    private String reviewTitle;

    @Size(min=8, message = "Size.submitReview.content")
    private String reviewContent;

    @NotNull(message = "NotNull.property")
    @Max(value=10, message = "Size.submitReview.reviewRatingMax")
    @Min(value=1, message = "Size.submitReview.reviewRatingMin")
    private Integer reviewRating;

    private Boolean replayability;

    @Pattern(regexp = "pc|xbox|nintendo|ps|^$", flags = Pattern.Flag.CASE_INSENSITIVE,message = "Pattern.submitReview.platform")
    private String platform;

    @Pattern(regexp = "hard|medium|easy|^$", flags = Pattern.Flag.CASE_INSENSITIVE, message = "Pattern.submitReview.difficulty")
    private String difficulty;

    private Boolean completed;

    @Max(value = 100000000, message = "Size.submitReview.gameMax")
    @Min(value = 0,message = "Size.submitReview.gameMin")
    private Double gameLength;

    @Pattern(regexp = "hours|minutes", flags = Pattern.Flag.CASE_INSENSITIVE, message = "Pattern.submitReview.timeunit")
    private String unit;

    public void setReplayability(Boolean replayability) {
        this.replayability = replayability;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public void setCompleted(Boolean completed) {
        this.completed = completed;
    }

    public void setGameLength(Double gameLength) {
        this.gameLength = gameLength;
    }

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

    public int getReviewRating() {
        return reviewRating;
    }

    public void setReviewRating(int reviewRating) {
        this.reviewRating = reviewRating;
    }

    public Boolean getReplayability() {
        return replayability;
    }

    public String getPlatform() {
        return platform;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public Boolean getCompleted() {
        return completed;
    }

    public Double getGameLengthSeconds() {
        if (gameLength == null) return null;
        if (getUnitEnum() == null) throw new UnitNotSelectedException();
        return getUnitEnum().toSeconds(gameLength);
    }
    public Double getGameLength() {
        return gameLength;
    }

    public Difficulty getDifficultyEnum() {
        if (difficulty == null || difficulty.isEmpty()) return null;
        return Difficulty.valueOf(getDifficulty().toUpperCase());
    }

    public Platform getPlatformEnum() {
        if (platform == null || platform.isEmpty()) return null;
        return Platform.valueOf(getPlatform().toUpperCase());
    }

    public GamelengthUnit getUnitEnum() {
        if (unit == null || unit.isEmpty()) return null;
        return GamelengthUnit.valueOf(getUnit().toUpperCase());
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

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
