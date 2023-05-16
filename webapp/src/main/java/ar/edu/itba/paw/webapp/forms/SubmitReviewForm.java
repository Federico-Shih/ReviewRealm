package ar.edu.itba.paw.webapp.forms;

import ar.edu.itba.paw.enums.Difficulty;
import ar.edu.itba.paw.enums.GamelengthUnit;
import ar.edu.itba.paw.enums.Platform;
import ar.edu.itba.paw.exceptions.UnitNotSelectedException;

import javax.validation.constraints.*;

public class SubmitReviewForm {

    @Override
    public String toString() {
        return "SubmitReviewForm{" +
                "reviewTitle='" + reviewTitle + '\'' +
                ", reviewContent='" + reviewContent + '\'' +
                ", reviewRating=" + reviewRating +
                ", replayability=" + replayability +
                ", platform='" + platform + '\'' +
                ", difficulty='" + difficulty + '\'' +
                ", completed=" + completed +
                ", gameLength=" + gameLength +
                '}';
    }

    @Size(min=8, max=100)
    private String reviewTitle;

    @Size(min=8, max=420)
    private String reviewContent;

    @NotNull
    @Max(value=10)
    @Min(value=1)
    private Integer reviewRating;

    private Boolean replayability;

    @Pattern(regexp = "pc|xbox|nintendo|ps|^$", flags = Pattern.Flag.CASE_INSENSITIVE)
    private String platform;

    @Pattern(regexp = "hard|medium|easy|^$", flags = Pattern.Flag.CASE_INSENSITIVE)
    private String difficulty;

    private Boolean completed;

    @Max(100000000)
    @Min(0)
    private Double gameLength;

    @Pattern(regexp = "hours|minutes", flags = Pattern.Flag.CASE_INSENSITIVE)
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

    public Integer getReviewRating() {
        return reviewRating;
    }

    public void setReviewRating(Integer reviewRating) {
        this.reviewRating = reviewRating;
    }

    public Boolean getReplayability() {
        return replayability != null ? replayability : false;
    }

    public String getPlatform() {
        return platform;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public Boolean getCompleted() {
        return completed != null ? completed : false;
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
        if (difficulty.equals("")) return null;
        return Difficulty.valueOf(getDifficulty().toUpperCase());
    }

    public Platform getPlatformEnum() {
        if (platform.equals("")) return null;
        return Platform.valueOf(getPlatform().toUpperCase());
    }

    public GamelengthUnit getUnitEnum() {
        if (unit.equals("")) return null;
        return GamelengthUnit.valueOf(getUnit().toUpperCase());
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }
}
