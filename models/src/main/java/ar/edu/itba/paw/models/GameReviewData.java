package ar.edu.itba.paw.models;

import ar.edu.itba.paw.enums.Difficulty;
import ar.edu.itba.paw.enums.Platform;

public class GameReviewData {
    private final double averageRating;
    private final Difficulty averageDifficulty;
    private final Platform averagePlatform;
    private final double averageGameTime;
    private final double replayability;
    private final double completability;

    public GameReviewData(double averageRating, Difficulty averageDifficulty,
                          Platform averagePlatform, double averageGameTime, double replayability,
                          double completability) {
        this.averageRating = averageRating;
        this.averageDifficulty = averageDifficulty;
        this.averagePlatform = averagePlatform;
        this.averageGameTime = averageGameTime;
        this.replayability = replayability;
        this.completability = completability;
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

    public String getAverageGameTimeStringHs() {
        return String.format("%.2f", averageGameTime/3600);
    }

    public String getReplayabilityString() {
        return String.format("%.2f", replayability);
    }

    public String getCompletabilityString() {
        return String.format("%.2f", completability);
    }
}
