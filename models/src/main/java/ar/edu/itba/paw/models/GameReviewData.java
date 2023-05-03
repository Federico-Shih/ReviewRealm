package ar.edu.itba.paw.models;

import ar.edu.itba.paw.enums.Difficulty;
import ar.edu.itba.paw.enums.Platform;

import java.util.List;

public class GameReviewData {
    private final List<Review> reviewList;
    private final double averageRating;
    private final Difficulty averageDifficulty;
    private final Platform averagePlatform;

    private final double averageGameTime;

    private final double replayability;

    private final double completability;

    public GameReviewData(List<Review> reviewList, double averageRating, Difficulty averageDifficulty,
                          Platform averagePlatform, double averageGameTime, double replayability,
                          double completability) {
        this.reviewList = reviewList;
        this.averageRating = averageRating;
        this.averageDifficulty = averageDifficulty;
        this.averagePlatform = averagePlatform;
        this.averageGameTime = averageGameTime;
        this.replayability = replayability;
        this.completability = completability;
    }

    public List<Review> getReviewList() {
        return reviewList;
    }

    public double getAverageRating() {
        return averageRating;
    }

    public Difficulty getAverageDifficulty() {
        return averageDifficulty;
    }

    public Platform getAveragePlatform() {
        return averagePlatform;
    }

    public double getAverageGameTime() {
        return averageGameTime;
    }

    public double getReplayability() {
        return replayability;
    }

    public double getCompletability() {
        return completability;
    }

    public String getAverageRatingString() {
        return String.format("%.2f", averageRating);
    }
}
